require 'minitest/autorun'
require_relative '../prospector.rb'

class ProspectorTest < Minitest::Test

  # Mock a Location object to remove dependency on the Location class
  def setup
    @dummy_location = Minitest::Mock.new('nowhere')
    def @dummy_location.name; 'Nowhere'; end
  end

  # UNIT TESTS FOR day_plural() METHOD
  # Equivalence classes:
  # 1) Input is 1 or -1: return 'day'
  # 2) Input is any other number: return 'days'
  # 3) Invalid input: raise an exception

  # Test first equivalence class
  def test_singular_days
    assert_equal Prospector.day_plural(1), 'day'
    assert_equal Prospector.day_plural(-1), 'day'
  end

  # Test second equivalence class
  def test_plural_days
    assert_equal Prospector.day_plural(2), 'days'
    assert_equal Prospector.day_plural(0), 'days'
  end

  # EDGE CASE
  # Test third equivalence class
  def test_nonnumeric_days
    assert_raises RuntimeError do
      Prospector.day_plural('foo')
    end
  end

  # UNIT TESTS FOR plural() METHOD
  # Equivalence classes:
  # 1) Input is 1 or -1: return 'ruby'
  # 2) Input is any other number: return 'rubies'
  # 3) Invalid input: raise an exception

  # Test first equivalence class
  def test_singular_rubies
    assert_equal Prospector.plural(1), 'ruby'
    assert_equal Prospector.plural(-1), 'ruby'
  end

  # Test second equivalence class
  def test_plural_rubies
    assert_equal Prospector.plural(2), 'rubies'
    assert_equal Prospector.plural(0), 'rubies'
  end

  # EDGE CASE
  # Test third equivalence class
  def test_nonnumeric_rubies
    assert_raises RuntimeError do
      Prospector.plural('bar')
    end
  end

  # UNIT TESTS FOR declare_victory? METHOD
  # Equivalence classes:
  # First:  @rubies is 0: print 'Going home empty-handed.'
  # Second: @rubies is between 1 and 9: print 'Going home sad.'
  # Third:  @rubies is 10 or greater: print 'Going home victorious!'

  # Test first equivalence class
  def test_zero_rubies_defeat_msg
    empty_handed_miner = Prospector.new(0, @dummy_location, 0, rubies = 0, fakes = 0)
    assert_output ("Going home empty-handed.\n") {
      empty_handed_miner.declare_victory?
    }
  end

  # Test second equivalence class
  def test_1_to_9_rubies_defeat_msg
    lazy_miner = Prospector.new(0, @dummy_location, 0, rubies = 5, fakes = 100)
    assert_output ("Going home sad.\n") {
      lazy_miner.declare_victory?
    }
  end

  # Test third equivalence class
  def test_10_or_greater_rubies_victory_msg
    prosperous_miner = Prospector.new(0, @dummy_location, 0, rubies = 100, fakes = 0)
    assert_output ("Going home victorious!\n") {
      prosperous_miner.declare_victory?
    }
  end

  # UNIT TESTS FOR continue?
  # Equivalence classes:
  # There are still turns remaining: return truthy
  # There are no turns remaining: return falsy

  # Test that prospectors with no remaining turns will indicate that
  # they should stop digging
  def test_continue_falsy
    turns_expired_miner = Prospector.new(0, @dummy_location, num_turns = 0)
    refute turns_expired_miner.continue?
  end

  # Test that prospectors with 1 turn remaining will correctly indicate that they
  # will continue to dig
  def test_continue_truthy
    turns_remaining_miner = Prospector.new(0, @dummy_location, num_turns = 1)
    assert turns_remaining_miner.continue?
  end

  # UNIT TEST FOR announce_start
  # Simple method should print Rubyist ID and starting location name

  # Test that output formatting is correct
  def test_announce_start_formatting
    nascent_miner = Prospector.new(1, @dummy_location, 0)
    assert_output "Rubyist #1 starting in #{@dummy_location.name}.\n" do
      nascent_miner.announce_start
    end
  end

  # UNIT TESTS for migrate
  # Equivalence classes:
  # 1) Turns left is less than 0:
  #   Raise an error
  # 2) Turns left is 0 or 1:
  #   Return with no output, @turns_left becomes/stays 0, do not change @location
  # 3) Turns left is 2 or greater:
  #   @turns_left decrements, change @location to random neighbor,
  #   Print [TAB]'Heading from [old_location_name] to [new_location_name].'[NEWLINE]

  # EDGE CASE
  # Test first equivalence class
  def test_migrate_negative_turns
    corrupt_miner = Prospector.new(1, @dummy_location, -1)
    assert_raises RuntimeError do
      corrupt_miner.migrate
    end
  end

  # Test second equivalence class with input of 0
  def test_migrate_0_turns
    finished_miner = Prospector.new(1, @dummy_location, 0)
    assert_silent { finished_miner.migrate }
    assert_equal 0, finished_miner.turns_left
  end

  # Test second equivalence class with input of 1
  def test_migrate_1_turn
    finished_miner = Prospector.new(1, @dummy_location, 1)
    assert_silent { finished_miner.migrate }
    assert_equal 0, finished_miner.turns_left
  end

  # Test third equivalence class with the boundary value 2
  def test_migrate_2_turns

    # mocks a second location and stubs neighbor randomization to isolate migration behavior
    def @dummy_location.random_neighbor?;
      n = Minitest::Mock.new('nowhere 2')
      def n.name; 'Vortex of neighboring emptiness'; end
      n
    end

    ambulatory_miner = Prospector.new(1, @dummy_location, 2)

    assert_output "Heading from #{@dummy_location.name} to Vortex of neighboring emptiness.\n" do
      ambulatory_miner.migrate
    end
    assert_equal 1, ambulatory_miner.turns_left
    assert_equal 'Vortex of neighboring emptiness', ambulatory_miner.location.name
  end

  # UNIT TESTS for dig
  # Equivalence classes:
  # Path A:  Rubyists migrate if the do not find any real or fake rubies in the specified location.
  # => real & fake are zero, so migrate is called.
  # Path B: Rubyists will remain in the current location if they find any number of real/fake rubies.
  # => either real or fake (or both) is nonzero, so migrate is not called.
  # => side effects for path B: real/fake rubies incremented by the appropriate #

  # Side effect for both paths: days increments
  # In both cases, location.mine is called once and format_output is called once

  # test that digging 0 rubies and 0 fake rubies outputs results and invokes migrate
  # This is (suboptimally) tested indirectly by checking for migrate's output
  def test_dig_path_a

    # stub location's mine method to always return no rubies and no fake rubies
    barren_location = Minitest::Mock.new('barren')
    def barren_location.name; 'Barren Landscape'; end
    def barren_location.mine; return 0, 0; end
    def barren_location.random_neighbor?; self; end

    miner = Prospector.new(1, barren_location, 10)
    assert_output "\tFound no rubies or fake rubies in Barren Landscape.\nHeading from Barren Landscape to Barren Landscape.\n" do
      miner.dig
    end
  end

  # Test that digging 1 fake ruby outputs results but does not invoke migrate
  # This is (suboptimally) tested indirectly by the lack of migrate output
  def test_dig_path_b

    # stub location's mine method to always return 1 fake rubie
    faketown = Minitest::Mock.new('fakeout')
    def faketown.name; 'Faketown'; end
    def faketown.mine; return 0, 1; end
    def faketown.random_neighbor?; self; end

    miner = Prospector.new(1, faketown, 10)
    assert_output "\tFound 1 fake ruby in Faketown.\n" do
      miner.dig
    end
  end

  # Test that prospector attributes (days, rubies, fakes) are updated correctly
  def test_dig_side_effects
    normaltown = Minitest::Mock.new('loc')
    def normaltown.name; 'Normaltown'; end
    def normaltown.mine; return 2, 3; end
    def normaltown.random_neighbor?; self; end

    miner = Prospector.new(1, normaltown, 10, rubies = 5, fakes = 5)

    # not ideal, but suppresses spurious console IO side effects from dig
    assert_output "\tFound 2 rubies and 3 fake rubies in Normaltown.\n" do
      miner.dig
    end

    # these 3 attributes are the actual target of this test
    assert_equal miner.days, 1
    assert_equal miner.rubies, 7
    assert_equal miner.fakes, 8
  end

  # UNIT TESTS for format_output
  #
  # Equivalence classes:
  #
  # Have no rubies or fake rubies:
  #   return [TAB]'Found no rubies or fake rubies in [location_name].'
  # Have only rubies:
  #   return [TAB]'Found [correct plural rubies] in [location_name].'
  # Have only fake rubies:
  #   return [TAB]'Found [correct plural fake rubies] in [location_name].'
  # Have 1 or more rubies and fake rubies:
  #   return [TAB]'Found [correct plural rubies] and [correct plural fake rubies] in [location_name].'

  # Test 0 rubies, 0 fake rubies
  def test_empty_handed_output
    empty_handed_miner = Prospector.new(0, @dummy_location, 0, rubies = 0, fakes = 0)
    assert_equal "\tFound no rubies or fake rubies in #{@dummy_location.name}.",
                 empty_handed_miner.format_output(empty_handed_miner.rubies, empty_handed_miner.fakes)
  end

  # Test 0 rubies, 2 fake rubies
  def test_only_fakes_output
    gullible_miner = Prospector.new(0, @dummy_location, 0, rubies = 0, fakes = 2)
    assert_equal "\tFound 2 fake rubies in #{@dummy_location.name}.",
                 gullible_miner.format_output(gullible_miner.rubies, gullible_miner.fakes)
  end

  # Test 2 rubies, 0 fake rubies
  def test_only_rubies_output
    lucky_miner = Prospector.new(0, @dummy_location, 0, rubies = 2, fakes = 0)
    assert_equal "\tFound 2 rubies in #{@dummy_location.name}.",
                 lucky_miner.format_output(lucky_miner.rubies, lucky_miner.fakes)
  end

  # Test 2 rubies, 2 fake rubies
  def test_rubies_and_fakes_output
    mixed_miner = Prospector.new(0, @dummy_location, 0, rubies = 2, fakes = 2)
    assert_equal "\tFound 2 rubies and 2 fake rubies in #{@dummy_location.name}.",
                 mixed_miner.format_output(mixed_miner.rubies, mixed_miner.fakes)
  end

  # UNIT TEST FOR report_haul
  # There aren't really applicable equivalence classes for this
  # This is just a simple method which prints rubies and fake rubies
  # found over the number of days the prospector has mined. 

  # Included one simple test for formatting mainly to increase code coverage
  def test_report_haul
    miner = Prospector.new(1, @dummy_location, 0, rubies = 2, fakes = 2)
    assert_output "After 0 days, Rubyist #1 found:\n\t2 rubies.\n\t2 fake rubies.\n" do
      miner.report_haul
    end
  end
end
