require 'minitest/autorun'
require_relative '../board.rb'

class BoardTest < Minitest::Test

  def setup
    @board = Board.new(0)
  end

  # UNIT TESTS FOR board initializers
  #
  # Since all the initializers execute together, they are tested as one unit
  # That is, this is basically just testing the Board constructor
  #
  # It doesn't reduce to equivalence classes very neatly because the constructor
  # just produces a representation of a hard-coded graph. It should always produce
  # the same result (except the value of seed used to make the Random object)
  #
  # NOTE: These are just smoke tests for the general graph structure

  # test that the graph contains 7 vertices (Locations)
  def test_board_num_nodes
    assert_equal @board.graph.count, 7
  end

  # test that the graph contains 9 undirected edges (paths between Locations)
  # since edges are stored in the graph as directed, need 2x that many references (18)
  def test_board_num_edges
    count = 0
    @board.graph.each_value do |location|
      count += location.neighbors.size
    end
    assert_equal count, 18
  end

  # test that the 2 boards created with the same seed will generate the same numbers
  # when asked to produce random numbers from 1-999
  def test_board_random_seed
    board2 = Board.new(0)
    rand1 = @board.random.rand(1000)
    rand2 = board2.random.rand(1000)
    assert_equal rand1, rand2
  end

  # UNIT TESTS FOR start_city?
  # This method should always return a reference to Enumerable Canyon
  def test_start_city
    assert_equal 'Enumerable Canyon', @board.start_city?.name
  end

end