require 'sinatra'
require 'sinatra/reloader'

# load truth table parameters input form
get '/' do
  erb :index
end

# handle form input to build the truth table
post '/table/' do
  t = params['true']
  f = params['false']
  size = params['size']

  # assign default values as T, F and 3 if unassigned
  t = 'T' if t == ''
  f = 'F' if f == ''
  size = (size == '') ? 3 : size.to_i

  # reject invalid parameters -- display error page
  # NOTE: reject size > 15 because truth table growth is EXPONENTIAL
  # 	--> implicit requirement that system must be stable!
  if t.size > 1 || f.size > 1 || size < 2 || t == f || size > 15
    erb :param_error
  else
    build_and_display_table t, f, size
  end
end

# redirect to main page if access this directly (need params)
get '/table/' do
  redirect '/'
end

# build a truth table of all binary strings of length size
# use t and f as symbols to display true and false values
def build_and_display_table(t, f, size)
  binary_strings = generate_binary_strings(size)
  values = calculate_values(binary_strings)

  erb :table, :locals => 
    {t:t, f:f, size:size, binary_strings:binary_strings, values:values}
end

# handle invalid addresses
not_found do
  status 404
  erb :error
end

# and returns true iff if all elements are true
def and?(input)
  input.each_char do |x|
    return false unless x == '1'
  end
  true
end

# or returns true iff if at least one element is true 
def or?(input)
  input.each_char do |x|
    return true if x == '1'
  end
  false
end

# xor returns true iff the number of true elements is odd
def xor?(input)
  count = 0
  input.each_char do |x|
    count += 1 if x == '1'
  end
  return count.odd?
end

# single returns true iff only one element is true
def single?(input)
  count = 0
  input.each_char do |x|
    if x == '1'
      count += 1
      return false if count > 1
    end
  end
  return count == 1
end

# returns a new list of all binary strings up to size in length
# Example: size 2 gives a list [ '00', '01', '10', '11' ]
def generate_binary_strings(size)
  strings = []
  num_strings = 2 ** size
  (0...num_strings).each do |value|
    string = sprintf "%0#{size}b", value
    strings << string
  end
  strings
end

# build a list of each truth table result for each binary string in strings
# return format is [and, or, nand, nor, xor, single] for each entry
# Example: if strings is ['010']
# Results in [ [false, true, false, true, true, true] ]
def calculate_values(strings)
  values = []
  strings.each do |str|
    and_val = and?(str)
    or_val = or?(str)
    val_set = [ and_val, or_val, !and_val, !or_val, xor?(str), single?(str) ]
    values << val_set
  end
  values
end
