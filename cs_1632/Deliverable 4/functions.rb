# frozen_string_literal: true

require_relative 'BlockFormatError.rb'
require_relative 'hash.rb'

NANOSECONDS_IN_A_SECOND = 1_000_000_000
MOD_VALUE = 65_536
HEX = 16

###################################################################
# IF THE BLOCKCHAIN IS VALID, THEN
# parsed_lines[0] will be the block number
# parsed_lines[1] will be the previous block hash
# parsed_lines[2] will be the sequence of transactions
# parsed_lines[3] will be the timestamp
# parsed_lines[4] will be the hash of the current block
#
# returns true if blockchain is valid, otherwise raises errors
###################################################################
def validate_file(file_list, timestamp, prev_hash)
  acc_balances = {}
  acc_balances.default = 0
  prepared_hashes = PreHash.new
  file_list.each_with_index do |line, line_no|
    parsed_lines = split_line line, line_no
    check_block_number parsed_lines[0], line_no
    verify_previous_hash prev_hash, parsed_lines[1], line_no
    timestamp = verify_and_return_datetime timestamp, parsed_lines[3], line_no
    verify_transactions parsed_lines[2], line_no, acc_balances
    hash_string = "#{parsed_lines[0]}|#{parsed_lines[1]}|#{parsed_lines[2]}|#{parsed_lines[3]}"
    prev_hash = verify_current_hash hash_string, parsed_lines[4], line_no, prepared_hashes
  end
  print_balances acc_balances
  true
end

def verify_parameters(args)
  raise ArgumentError unless args.count == 1
  raise IOError unless File.exist? args[0]

  true
end

def split_line(line, line_no)
  result = line.strip.split('|')
  raise BlockFormatError, "Line #{line_no}: Invalid block format." unless result.count == 5

  result
end

# Naive version of the hash function (takes string input)
# Returns a 4 digit hexadecimal string
def calculate_hash(input, prep_hashes)
  result = 0
  input.unpack('U*').each do |x|
    result += prep_hashes.hash(x)
  end
  (result % MOD_VALUE).to_s(HEX)
end

# confirms that block number matches line number
# returns true if so, otherwise raises BlockFormatError
def check_block_number(block_no, line_no)
  begin
    val = Integer(block_no)
  rescue ArgumentError, TypeError
    val = nil
  end

  unless val == line_no
    raise BlockFormatError, "Line #{line_no}: Invalid block number #{block_no}, should be #{line_no}"
  end

  true
end

# parse the datestring into seconds and nanoseconds
# raises errors if format is invalid or the new time is not stricly later than previous_time
# otherwise, returns the newly verified time as [seconds, nanoseconds], both as integers
def verify_and_return_datetime(prev_time, datestring, line_no)
  curr_time = datestring.split('.')

  raise BlockFormatError, "Line #{line_no}: Invalid timestamp format." unless curr_time.count == 2

  begin
    curr_time[0] = Integer(curr_time[0])
    curr_time[1] = Integer(curr_time[1])
  rescue ArgumentError, TypeError
    curr_time = nil
  end

  raise BlockFormatError, "Line #{line_no}: Invalid timestamp format." if curr_time.nil?

  unless curr_time[1] < NANOSECONDS_IN_A_SECOND
    raise BlockFormatError, "Line #{line_no}: Invalid timestamp format -- invalid number of nanoseconds."
  end

  invalid = (prev_time[0] > curr_time[0]) || (prev_time[0] == curr_time[0] && prev_time[1] >= curr_time[1])

  if invalid
    raise BlockFormatError, "Line #{line_no}: Previous timestamp #{prev_time[0]}.#{prev_time[1]} >= \
new timestamp #{curr_time[0]}.#{curr_time[1]}"
  end

  curr_time
end

# previous hash => the hash value that the verifier has stored in memory from the last block
# target hash => the hash that the current block's previous_hash field actually shows
# if they're not the same, raise an error
def verify_previous_hash(previous_hash, target_hash, line_no)
  unless previous_hash.eql? target_hash
    raise BlockFormatError, "Line #{line_no}: Previous hash was #{target_hash}, should be #{previous_hash}"
  end

  previous_hash
end

# target hash => the hash that the current block's current_hash field actually shows
def verify_current_hash(hash_string, target_hash, line_no, prepared_hashes)
  current_hash = calculate_hash hash_string, prepared_hashes

  unless current_hash.eql? target_hash
    raise BlockFormatError, "Line #{line_no}: String \'#{hash_string}\' \
hash set to #{target_hash}, should be #{current_hash}"
  end

  current_hash
end

# transactions => string of account numbers and billcoins from one line of the file
# line_no => the line number we are currently on in the file
# account_balances => hashmap of the accounts and current billcoin balances
def verify_transactions(transactions, line_no, acc_balances)
  transactions_arr = transactions.split(':')
  transactions_arr.each do |x|
    unless x[6] == '>' && x[13] == '(' && x[-1] == ')'
      raise BlockFormatError, "Line #{line_no}: Could not parse transactions list '#{transactions}'"
    end

    from_addr = x[0..5]
    unless from_addr == 'SYSTEM' || ((from_addr =~ /\D/).nil? || from_addr.size != 6)
      raise BlockFormatError, "Line #{line_no}: Could not parse transactions list '#{transactions}'"
    end

    if x == transactions_arr.last && from_addr != 'SYSTEM'
      raise BlockFormatError, "Line #{line_no}: Could not parse transactions list '#{transactions}'"
    end

    to_addr = x[7..12]
    unless (to_addr =~ /\D/).nil? || to_addr.size != 6
      raise BlockFormatError, "Line #{line_no}: Could not parse transactions list '#{transactions}'"
    end

    coins = x.split('(').last.split(')').first

    begin
      coins = Integer(coins)
    rescue ArgumentError, TypeError
      coins = nil
    end

    if coins.nil? || coins <= 0
      raise BlockFormatError, "Line #{line_no}: Could not parse transactions list '#{transactions}'"
    end

    give_coins(from_addr, to_addr, coins, acc_balances)
  end
  validate_balances(acc_balances, line_no)
end

# from_addr => the address that is sending the billcoins
# to_addr => the address that is receiving the billcoins
# acc_balances => hashmap of the accounts and current billcoin balances
def give_coins(from_addr, to_addr, coins, acc_balances)
  acc_balances[from_addr] = acc_balances[from_addr].to_i - coins.to_i
  acc_balances[to_addr] = acc_balances[to_addr].to_i + coins.to_i

  acc_balances
end

# acc_balances => hashmap of the accounts and current billcoin balances
# line_no => the line number we are currently on in the file
def validate_balances(acc_balances, line_no)
  acc_balances.each_key do |x|
    if (acc_balances[x]).negative? && x != 'SYSTEM'
      raise BlockFormatError, "Line #{line_no}: Invalid block, address #{x} has #{acc_balances[x]} billcoins!"
    end
  end

  acc_balances
end

# prints account balances after blockchain is validated
# will only print accounts which have a strictly positive balance
# prints accounts sorted by account number (ascending)
def print_balances(acc_balances)
  acc_balances.keys.sort.each do |acc_no|
    balance = acc_balances[acc_no]
    puts "#{acc_no}: #{balance} billcoins" if balance.positive?
  end
end
