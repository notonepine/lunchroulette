class SearchingUser < ActiveRecord::Base
    acts_as_mappable default_units: :kms, lat_column_name: :latitude, lng_column_name: :longitude
    #attr_accessible :name, :latitude, :longitude, :user_id
    belongs_to :user
    belongs_to :meetup

    validates :latitude, :longitude, presence: true

    alias_attribute :lat, :latitude
    alias_attribute :lng, :longitude

    def self.find_meeting_point(matches)
        average_lat = 0
        average_long = 0
        matches.each do |match|
            average_lat += match.latitude
            average_long += match.longitude
        end
        average_long /= matches.length
        average_lat /= matches.length
        {lat: average_lat, long: average_long}
    end

    def self.find_restaurant(location)
        latitude = location[:lat]
        longitude = location[:long]
        key = "AIzaSyDQUKLFkCDekxYmRemK5sQyV38sPoabh6Q"
        base_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        base_url << "location=#{latitude},#{longitude}"
        base_url << "&key=#{key}"
        base_url << "&radius=1000"
        base_url << "&sensor=true"
        base_url << "&rankby=prominence"
        base_url << "&types=restaurant"
        response = Net::HTTP.get(URI(base_url))
        response = JSON.parse(response)
        options = response["results"].select{|r| r["opening_hours"] && r["opening_hours"]["open_now"]}
        binding.pry
        choice = options.slice(0, 5)[0 + rand(4)]
        {lat: choice["geometry"]["location"]["lat"], long: choice["geometry"]["location"]["long"], name: choice["name"], price: choice["price_level"], photo_reference: (choice["photos"] ? choice["photos"].first["photo_reference"] : "")}
    end
end
