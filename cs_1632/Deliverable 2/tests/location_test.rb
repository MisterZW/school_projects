require 'minitest/autorun'
require_relative '../location.rb'

class LocationTest < Minitest::Test

  def setup
    @random = Random.new
    @test_neighbor_list = [
      Location.new('TOWN 1', 0, 4, @random),
      Location.new('TOWN 2', 1, 2, @random),
      Location.new('TOWN 3', 2, 2, @random),
      Location.new('TOWN 4', 3, 1, @random),
      Location.new('TOWN 5', 4, 0, @random)
    ]
    @test_loc = Location.new('Testtown', 3, 3, @random)
  end

  # UNIT TESTS FOR METHOD connect
  # Equivalence classes:
  # valid Location object provided: this location shall update its internal
  # 	adjacency list to include the new neighbor
  # invalid parameter: raise exception	

  # connecting a valid Location to a location with 0 neighbors shall yield
  # a location which has one neighbor
  def test_connect_increments_neighbor_count
    @test_loc.connect @test_neighbor_list[0]
    assert_equal 1, @test_loc.neighbors.count
  end

  # attempting to connect a nonlocation should raise an exception
  def test_connect_invalid_object
  	assert_raises RuntimeError do
      @test_loc.connect 'foo'
    end
  end

  # UNIT TESTS FOR METHOD random_neighbor?
  # Equivalence classes:
  # location has no neighbors: return nil
  # location has one/more neighbors: return reference to random neighbor

  # calling random_neighbor? must produce a valid, connected Location
  # due to randomness element, sample five times
  def test_random_neighbor_is_valid
    @test_neighbor_list.each do |neighbor|
      @test_loc.connect neighbor
    end
  	5.times { assert_includes @test_neighbor_list, @test_loc.random_neighbor? }
  end

  # Test that calling random_neighbor? on a location with no neighbors produces nil
  def test_random_neighbor_with_no_neighbors
  	loc = Location.new('Lonely City')
  	assert_nil loc.random_neighbor?
  end

  # UNIT TESTS FOR METHOD mine
  #
  # No real equivalence classes as there are no parameters,
  # though results will vary due to randomization
  #
  # Success: returns a length 2 array, where the two values are
  #		[rubies, fake_rubies] and rubies and fake rubies are values
  # 	in range 0 to @max_rubies and 0 to @max_fake_rubies
  #
  # Failure: invalid return format or invalid values for number of
  # 	rubies or fake rubies mined

  # ensure correct return format (Array)
  def test_mine_returns_array
    mine_results = @test_loc.mine
    assert mine_results.is_a? Array
  end

  # ensure mine returns two and only two values
  def test_mine_returns_two_values
    mine_results = @test_loc.mine
    assert_equal mine_results.count, 2
  end

  # Make sure rubies returned by mine are between 0 and @max_rubies
  # due to randomness element, sample five times
  def test_ruby_results_valid
    5.times do
      mine_results = @test_loc.mine
      assert_includes (0..@test_loc.max_rubies), mine_results[0]
    end
  end

  # Make sure fake rubies returned are between 0 and @max_fake_rubies
  # due to randomness element, sample five times
  def test_fake_ruby_results_valid
    5.times do
      mine_results = @test_loc.mine
      assert_includes (0..@test_loc.max_fake_rubies), mine_results[1]
    end
  end
end