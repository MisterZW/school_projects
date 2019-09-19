# frozen_string_literal: true

require 'simplecov'
require 'minitest/autorun'
SimpleCov.start

require_relative 'functions.rb'
require_relative 'hash.rb'

# Test suite for the verifier.rb program
class VerifierTest < Minitest::Test
  ############################################################################
  # UNIT TESTS for verify_parameters(args)
  #
  # args represents ARGV and is only valid IF
  # a) there is only one command line argument
  # AND
  # b) that command line argument is a valid filename
  #
  # Returns true if valid, otherwise raise an exception
  #
  ############################################################################

  def test_no_parameters
    assert_raises ArgumentError do
      verify_parameters([])
    end
  end

  def test_two_parameters
    assert_raises ArgumentError do
      verify_parameters(%w[mom dad])
    end
  end

  # this file must exist because it is THIS file
  def test_existing_file
    assert verify_parameters(['verifier_tests.rb'])
  end

  def test_no_filename
    assert_raises IOError do
      verify_parameters([''])
    end
  end

  def test_invalid_filename
    assert_raises IOError do
      verify_parameters(['hamburglar.txt.rb'])
    end
  end

  ##########################################################################
  # UNIT TESTS split_line(line, line_no)
  #
  # splits string line into a 5-element list on the pipe delimiter: |
  # should throw an BlockFormatError if the list count is wrong
  # line_no is only used in error reporting
  ##########################################################################

  def test_valid_line
    res = split_line('0|0|SYSTEM>569274(100)|1553184699.650330000|288d', 0)
    assert res.is_a? Array
  end

  def test_invalid_line_too_short
    assert_raises BlockFormatError do
      split_line('0|0|SYSTEM>569274(100)|1553184699.650330000', 0)
    end
  end

  def test_invalid_line_too_long
    assert_raises BlockFormatError do
      split_line('0|0|SYSTEM>569274(100)|1553184699.650330000|288d|dog', 0)
    end
  end

  def test_empty_line
    assert_raises BlockFormatError do
      split_line('', 0)
    end
  end

  ############################################################################
  # UNIT TESTS FOR check_block_number(block_no, line_no)
  #
  # block number should be incoming as a string representing a valid integer X
  # line number should be an integer equal to X, else the block is invalid
  ############################################################################

  def test_valid_block_number
    assert check_block_number('0', 0)
  end

  def test_block_number_mismatch
    assert_raises BlockFormatError do
      check_block_number('2', 0)
    end
  end

  def test_block_number_not_numeric
    assert_raises BlockFormatError do
      check_block_number('0a', 0)
    end
  end

  ###############################################################################
  # UNIT TESTS FOR verify_and_return_datetime(prev_time, datestring, line_no)
  #
  # prev_time is in format [integer, integer] representing [seconds, nanoseconds]
  # datestring should be a string in format 'seconds.nanoseconds'
  # line_no should be an integer representing block # => used for error reporting
  ###############################################################################

  def test_simple_valid_datetime
    assert_equal [11, 0], verify_and_return_datetime([10, 100], '11.0', 10)
  end

  def test_close_valid_datetime
    assert_equal [1, 2], verify_and_return_datetime([1, 1], '1.2', 10)
  end

  def test_equal_datetimes_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([1, 1], '1.1', 0)
    end
  end

  def test_earlier_datetime_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([12, 100], '10.50', 5)
    end
  end

  def test_earlier_close_datetime_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([12, 100], '12.99', 5)
    end
  end

  def test_bad_args_count_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([12, 100], '129923', 5)
    end
  end

  def test_bad_datatype_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([12, 100], 'foo.bar', 5)
    end
  end

  def test_too_many_nanoseconds_invalid
    assert_raises BlockFormatError do
      verify_and_return_datetime([12, 100], '1000000001', 3)
    end
  end

  # UNIT TEST FOR calculate_hash

  def test_calculate_hash
    assert_equal '288d', calculate_hash('0|0|SYSTEM>569274(100)|1553184699.650330000', PreHash.new)
  end

  # UNIT TEST for validate_file(file_list, timestamp, prev_hash)
  # returns true if blockchain is valid, otherwise raises errors

  # this is a baseline test on a few valid blocks from sample.txt
  # should return true (not raise any errors)
  def test_validate_file
    file_list = [
      '0|0|SYSTEM>569274(100)|1553184699.650330000|288d',
      '1|288d|569274>735567(12):735567>561180(3):735567>689881(2):SYSTEM>532260(100)|1553184699.652449000|92a2',
      '2|92a2|569274>577469(9):735567>717802(1):577469>402207(2):SYSTEM>794343(100)|1553184699.658215000|4d25'
    ]
    assert validate_file file_list, [0, 0], '0'
  end

  # UNIT TESTS FOR verify_previous_hash(previous_hash, target_hash, line_no)
  # returns previous_hash if valid, otherwise raises BlockFormatError

  def test_accept_prev_hash
    assert_equal '92a2', verify_previous_hash('92a2', '92a2', 3)
  end

  def test_reject_prev_hash
    assert_raises BlockFormatError do
      verify_previous_hash('92a1', '92a2', 3)
    end
  end

  # UNIT TESTS FOR verify_current_hash(hash_string, target_hash, line_no)
  # returns current_hash if valid, otherwise raises BlockFormatError

  def test_legit_current_hash
    hash_string = '10|cb0f|281974>443914(6):SYSTEM>562872(100)|1553188611.607041000'
    assert_equal 'd5e', verify_current_hash(hash_string, 'd5e', 10, PreHash.new)
  end

  # changing one character from above test should give a different hash result (fail)
  def test_incorrect_current_hash
    hash_string = '10|cb0f|281974>443914(6):SYSTEM>572872(100)|1553188611.607041000'
    assert_raises BlockFormatError do
      verify_current_hash(hash_string, 'd5e', 10, PreHash.new)
    end
  end

  # UNIT TESTS FOR verify_transactions(transactions, line_no, acc_balances)
  # returns the acc_balances hashmap if valid, otherwise raises BlockFormatError

  def test_valid_transactions
    transactions = '569274>735567(12):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    new_acc_balances = { '569274' => 88, '735567' => 10, '689881' => 102, 'SYSTEM' => -200 }
    assert_equal verify_transactions(transactions, 1, acc_balances), new_acc_balances
  end

  def test_invalid_transaction_format
    transactions = '569274735567(12):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  def test_invalid_from_addr_format
    transactions = '5aaa4>735567(12):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  def test_invalid_to_addr_format
    transactions = '569274>aa5567(12):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  def test_invalid_coin_format
    transactions = '569274>735567(aaa):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  def test_negative_coin_transaction
    transactions = '569274>735567(-12):735567>689881(2):SYSTEM>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, 'SYSTEM' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  def test_invalid_reward_transaction
    transactions = '569274>735567(12):735567>689881(2):111111>689881(100)'
    acc_balances = { '569274' => 100, '735567' => 0, '689881' => 0, '111111' => -100 }
    assert_raises BlockFormatError do
      verify_transactions(transactions, 1, acc_balances)
    end
  end

  # UNIT TESTS FOR give_coins(from_addr, to_addr, coins, acc_balances)
  # returns the acc_balances hashmap if valid, otherwise raises BlockFormatError

  def test_give_coins_balances
    acc_balances = { '111111' => 50, '222222' => 20 }
    new_acc_balances = { '111111' => -50, '222222' => 120 }
    assert_equal give_coins('111111', '222222', 100, acc_balances), new_acc_balances
  end

  # UNIT TESTS FOR validate_balances(acc_balances, line_no)
  # returns the acc_balances hashmap, otherwise raises BlockFormatError

  def test_validate_balances_valid
    acc_balances = { 'SYSTEM' => -150, '111111' => 100, '222222' => 50 }
    assert_equal validate_balances(acc_balances, 1), acc_balances
  end

  def test_validate_balances_invalid
    acc_balances = { 'SYSTEM' => -150, '111111' => -100, '222222' => 250 }
    assert_raises BlockFormatError do
      validate_balances(acc_balances, 1)
    end
  end

  # UNIT TESTS FOR print_balances(acc_balances)
  # outputs the account balances

  def test_print_balances
    acc_balances = { 'SYSTEM' => -150, '222222' => 100, '333333' => 50, '111111' => 250 }
    assert_output("111111: 250 billcoins\n222222: 100 billcoins\n333333: 50 billcoins\n") do
      print_balances(acc_balances)
    end
  end
end
