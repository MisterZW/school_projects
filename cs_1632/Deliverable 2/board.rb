require_relative 'location.rb'

############################################################
#
# This models Rubyland, where Rubyists Rush for Rubies!
#
# RUBYLAND MAP VISUALIZATION
#
# Nil Town-----------------------------
#      \                              |
#       \                             |
#   Monkey Patch City                 |
#        |     \       /---------- Hash Crossing
#        |     Matzburg----              |
#        |            |    \             |
#        |            |     \---------- Dynamic Palisades
#        |            \____
#        |                |
#  Enumerable Canyon ---- Duck Type Beach
#
############################################################
class Board
  attr_reader :graph
  attr_reader :random
  # initialize is split into helpers to avoid overlong method
  # warnings from Rubocop
  #
  # helpers methods for initialize are prepended 'initialize_'
  def initialize(seed)
    @random = Random.new(seed)

    # these are reference maps which aid in building the graph
    @town_names = initialize_town_names
    @town_connections = initialize_town_connections
    @treasure_map = initialize_treasure_map

    # this is the data structure which ultimately models the board
    @graph = {}
    initialize_nodes
    initialize_edges
  end

  # simple list of locations on the board
  def initialize_town_names
    [
      'Enumerable Canyon', 'Duck Type Beach', 'Monkey Patch City',
      'Matzburg', 'Dynamic Palisades', 'Hash Crossing', 'Nil Town'
    ]
  end

  # This is a list of directed edges in the board's graph
  # 'town_name' => ['neighbor_1', 'neighbor_2', ...]
  def initialize_town_connections
    {
      'Enumerable Canyon' => ['Monkey Patch City', 'Duck Type Beach'],
      'Duck Type Beach' => ['Enumerable Canyon', 'Matzburg'],
      'Monkey Patch City' => ['Enumerable Canyon', 'Nil Town', 'Matzburg'],
      'Matzburg' => ['Duck Type Beach', 'Monkey Patch City',
                     'Dynamic Palisades', 'Hash Crossing'],
      'Dynamic Palisades' => ['Matzburg', 'Hash Crossing'],
      'Hash Crossing' => ['Matzburg', 'Dynamic Palisades', 'Nil Town'],
      'Nil Town' => ['Monkey Patch City', 'Hash Crossing']
    }
  end

  # This maps location names to their respective treasure rates
  def initialize_treasure_map
    # [max_rubies, max_fake_rubies]
    {
      'Enumerable Canyon' => [1, 1], 'Duck Type Beach' => [2, 2],
      'Monkey Patch City' => [1, 1], 'Matzburg' => [3, 0],
      'Dynamic Palisades' => [2, 2], 'Hash Crossing' => [2, 2],
      'Nil Town' => [0, 3]
    }
  end

  # create all the nodes and add them to the graph
  def initialize_nodes
    @town_names.each do |name|
      rubies = @treasure_map[name][0]
      fake_rubies = @treasure_map[name][1]
      @graph[name] = Location.new(name, rubies, fake_rubies, @random)
    end
  end

  # link all neighbors together
  def initialize_edges
    @town_names.each do |name|
      @town_connections[name].each do |neighbor_name|
        @graph[name].connect @graph[neighbor_name]
      end
    end
  end

  # returns a reference to the starting location on the board
  #
  # the Rubyists need this reference so that they can traverse the
  # graph edges as they move around the board
  def start_city?
    @graph['Enumerable Canyon']
  end
end
