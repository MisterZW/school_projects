# This class represents a Rubyist (ruby-rusher)
class Prospector
  readable_attributes = %i[id name rubies fakes turns_left days location]
  attr_reader(*readable_attributes)

  def initialize(id, location, num_turns, rubies = 0, fakes = 0)
    @id = id
    @name = 'Rubyist #' + id.to_s
    @location = location
    @rubies = rubies
    @fakes = fakes
    @turns_left = num_turns
    @days = 0
  end

  # Perform one day/iteration digging at the current location,
  # report results, and move if no treasure is found
  def dig
    real, fake = @location.mine
    @days += 1
    @rubies += real
    @fakes += fake
    puts format_output(real, fake)
    migrate if real.zero? && fake.zero?
  end

  # Output prospector name and location
  def announce_start
    puts "#{@name} starting in #{@location.name}."
  end

  # Build a gramatically appropriate string to report Rubyist dig results
  def format_output(real, fake)
    result = if real.zero? && fake.zero?
               'no rubies or fake rubies'
             elsif real > 0 && fake.zero?
               "#{real} #{Prospector.plural(real)}"
             elsif real.zero? && fake > 0
               "#{fake} fake #{Prospector.plural(fake)}"
             else
               "#{real} #{Prospector.plural(real)} and #{fake} fake #{Prospector.plural(fake)}"
             end
    "\tFound " + result + " in #{@location.name}."
  end

  # move this prospector to a random adjoining location
  def migrate
    raise 'Turns left is invalid!' unless turns_left >= 0

    @turns_left = @turns_left.zero? ? 0 : turns_left - 1
    return if @turns_left.zero?

    new_location = @location.random_neighbor?
    puts "Heading from #{@location.name} to #{new_location.name}."
    @location = new_location
  end

  # class helper to get the right pluralization of the word ruby
  def self.plural(ruby_count)
    raise 'Need a NUMBER to pluralize!' unless ruby_count.is_a? Numeric

    ruby_count.abs == 1 ? 'ruby' : 'rubies'
  end

  # class helper to get the right pluralization of the word day
  def self.day_plural(day_count)
    raise 'Need a NUMBER to pluralize!' unless day_count.is_a? Numeric

    day_count.abs == 1 ? 'day' : 'days'
  end

  # display all the loot this prospector has collected
  def report_haul
    result = "After #{@days} #{Prospector.day_plural(@days)}, #{@name} found:\n"
    result += "\t#{@rubies} #{Prospector.plural(@rubies)}.\n"
    result += "\t#{@fakes} fake #{Prospector.plural(@fakes)}.\n"
    puts result
  end

  # display win/loss message for this prospector
  def declare_victory?
    if @rubies.zero?
      puts 'Going home empty-handed.'
    elsif @rubies < 10
      puts 'Going home sad.'
    else
      puts 'Going home victorious!'
    end
  end

  # signal to the main execution loop if turns have expired
  def continue?
    @turns_left > 0
  end
end
