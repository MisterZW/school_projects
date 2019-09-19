# frozen_string_literal: true

require 'flamegraph'

require_relative 'BlockFormatError.rb'
require_relative 'functions.rb'

Flamegraph.generate('verifier.html') do
  GC.disable
  begin
    verify_parameters ARGV
  rescue ArgumentError
    puts "Usage:\truby verifier.rb <name_of_file>\n\tname_of_file = name of file to verify"
    exit(1)
  rescue IOError
    puts ARGV[0] + ' is not a valid file. Please enter a valid filename.'
    exit(2)
  end
  begin
    validate_file IO.readlines(ARGV[0]), [0, 0], '0'
  rescue BlockFormatError => error
    puts error
    puts 'BLOCKCHAIN INVALID'
    exit(3)
  end
end
exit(0)
