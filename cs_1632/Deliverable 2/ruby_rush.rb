require_relative 'args.rb'
require_relative 'board.rb'
require_relative 'prospector.rb'

def ruby_rush(seed, num_prospectors, num_turns)
  board = Board.new(seed)
  (1..num_prospectors).each do |id_no|
    rubyist = Prospector.new(id_no, board.start_city?, num_turns)
    rubyist.announce_start
    rubyist.dig while rubyist.continue?
    rubyist.report_haul
    rubyist.declare_victory?
  end
end

usage_and_exit unless verify_args(ARGV)
ruby_rush(ARGV[0].to_i, ARGV[1].to_i, ARGV[2].to_i)
exit(0)
