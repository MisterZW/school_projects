# represents a mineable location on the board/graph
# represented as a node with an adjacency list
class Location
  readable_attributes = %i[name max_rubies max_fake_rubies neighbors]
  attr_reader(*readable_attributes)

  def initialize(name, max_rubies = 0, max_fake_rubies = 0, random = Random.new)
    @name = name.to_s
    @max_rubies = max_rubies
    @max_fake_rubies = max_fake_rubies
    @neighbors = []
    @random = random
  end

  # adds a edge between this location and another in the graph
  def connect(new_neighbor)
    raise 'Invalid Location Provided' unless new_neighbor.is_a? Location

    @neighbors.push(new_neighbor)
  end

  # returns a reference to a random neighboring location
  # returns nil if this location has no neighboring locations
  def random_neighbor?
    @neighbors.count.zero? ? nil : @neighbors[@random.rand(@neighbors.count)]
  end

  # mine this location for gems!
  # returns the number of real rubies and the number of fake rubies found
  def mine
    num_rubies = @random.rand(@max_rubies + 1)
    num_fake_rubies = @random.rand(@max_fake_rubies + 1)
    return num_rubies, num_fake_rubies
  end
end
