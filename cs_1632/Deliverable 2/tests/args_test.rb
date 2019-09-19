require 'minitest/autorun'
require_relative '../args.rb'

class ArgsTest < Minitest::Test

  # UNIT TESTS FOR verify_seed
  # Equivalence classes:
  # Seed can be converted to an integer: return truthy
  # Seed cannot be converted to an integer: return falsy

  # Test that a string of a negative integer value is treated as a valid integer
  def test_verify_int_valid_neg
    assert verify_int('-100')
  end

  # Test that a string of a positive integer value is treated as a valid integer
  def test_verify_int_valid_pos
    assert verify_int('12')
  end

  # Test that a string of a floating point value is treated as an invalid integer
  def test_verify_int_float
    refute verify_int('12.34')
  end

  # Test that a nonnumeric string is treated as an invalid integer
  def test_verify_seed_garbage
    refute verify_int('foo')
  end

  # UNIT TESTS FOR verify_num_prospectors
  # Equivalence classes:
  # string can be converted to int and is positive: return truthy
  # string can be converted to an int, but is negative: return falsy
  # string cannot be converted to an int: return falsy

  # Test that a string of a positive integer value is treated as a valid positive integer
  def test_verify_pos_int_valid
    assert verify_positive_int('4')
  end

  # Test that a string of a negative integer value is treated as an invalid positive integer
  def test_verify_pos_int_negative
    refute verify_positive_int('-1')
  end

  # Test that a string of a floating point value is treated as an invalid positive integer
  def test_verify_int_float
    refute verify_positive_int('12.34')
  end

  # Test that a nonnumeric string is treated as an invalid positive integer
  def test_verify_pos_int_garbage
    refute verify_positive_int('foo')
  end

  # UNIT TESTS FOR verify_args
  # Equivalence classes:
  # All three arguments are valid: return truthy
  # One or more arguments is invalid: return falsy

  # Test that using a valid argument for all parameters is considered valid (success case)
  def test_verify_args_all_valid
    assert verify_args(['1', '1', '1'])
  end

  # Test that using a valid argument for all parameters is considered valid even when
  # the seed value is negative
  def test_verify_args_all_valid_negative_seed
    assert verify_args(['-1', '1', '1'])
  end

  # Test that passing one invalid argument invalidates the result
  def test_verify_args_one_invalid
    refute verify_args(['1', '-1', '1'])
  end

  # Test that passing several invalid arguments invalidates the result
  def test_verify_args_mixed_2
    refute verify_args(['1', 'foo', '-22'])
  end

  # Test that passing all invalid arguments invalidates the result
  def test_verify_args_all_false
    refute verify_args(['foo', 'foo', 'foo'])
  end

  # Test that passing no arguments invalidates the result
  def test_verify_no_args
    refute verify_args([])
  end

  # Test that passing too few arguments invalidates the result
  def test_verify_too_few_args
    refute verify_args(['1', '1'])
  end

  # Display usage does not have any unit tests because it has only one
  # path and is just a totally deterministic series of print statements

  # usage_and_exit is not unit tested because it calls exit, which would
  # break the test run. This is not really a concern because of its 
  # obvious simplicity, however.

end