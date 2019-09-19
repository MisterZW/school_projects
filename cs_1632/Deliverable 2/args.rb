# A few helper functions to manage the user input for Ruby Rush

# checks if a string can reasonably be converted to an integer
def verify_int(string)
  Integer(string) rescue nil
end

# checks if a string can reasonably be converted to a positive integer
def verify_positive_int(string)
  num_prospectors = Integer(string) rescue nil
  return false unless num_prospectors

  num_prospectors >= 0
end

# returns truthy if arguments meet the program specifications to run Ruby Rush
# otherwise, returns falsy
def verify_args(args)
  return false unless args.count == 3

  verify_int(args[0]) && verify_positive_int(args[1]) && verify_positive_int(args[2])
end

# display proper program usage in the event of invalid command line arguments
def display_usage
  puts 'Usage:'
  puts 'ruby ruby_rush.rb *seed* *num_prospectors* *num_turns*'
  puts '*seed* should be an integer'
  puts '*num_prospectors* should be a non-negative integer'
  puts '*num_turns* should be a non-negative integer'
end

# called if arguments are invalid to terminate program with usage instructions
def usage_and_exit
  display_usage
  exit(1)
end
