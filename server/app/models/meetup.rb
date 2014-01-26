class Meetup < ActiveRecord::Base
    has_many :searching_users
    acts_as_mappable default_units: :kms, lat_column_name: :latitude, lng_column_name: :longitude
    alias_attribute :lat, :latitude
    alias_attribute :lng, :longitude

    PARTY_LIMIT = 4

    def check_ready(do_find_restaurant = true)
        if self.searching_users.length > PARTY_LIMIT - 1
            if do_find_restaurant
                restaurant = find_restaurant
                {restaurant: restaurant, matches: searching_users.map{|s| s.user_id}}
            else
                true
            end
        else
            false
        end
    end

    def check_finalized
        finalized = true
        searching_users.each do |user|
            finalized = finalized && user.finalized
        end
        if finalized
            searching_users.each do |u|
                u.destroy!
            end
            self.destroy!
        end
        finalized
    end

    def update_location
        average_lat = 0
        average_long = 0
        searching_users.each do |user|
            average_lat += user.latitude
            average_long += user.longitude
        end
        average_long /= searching_users.length
        average_lat /= searching_users.length
        update_attributes!(latitude: average_lat, longitude: average_long)
    end

    def find_restaurant
        latitude = self.latitude
        longitude = self.longitude
        if restaurant_latitude.present? && restaurant_longitude.present?
            return {lat: restaurant_latitude, long: restaurant_longitude, name: restaurant_name}#, photo_reference: (choice["photos"] ? choice["photos"].first()["photo_reference"] : "")}
        end
        su = searching_users.all
        #gonna find all the tags in common between all the people
        # eval_string = ""
        # su.each do |s|
        #     eval_string << "#{s.tags} &"
        # end
        # tags = eval(eval_string.slice(0..-2))
        # tag = ""
        # if tags.length > 0
        #     tag = tags[0 + rand(tags.length)]
        # end

        key = "AIzaSyDQUKLFkCDekxYmRemK5sQyV38sPoabh6Q"
        base_url = "https://maps.googleapis.com/maps/api/place/nearbysearch/json?"
        base_url << "location=#{latitude},#{longitude}"
        base_url << "&key=#{key}"
        base_url << "&radius=1000"
        base_url << "&sensor=true"
        base_url << "&rankby=prominence"
        base_url << "&types=restaurant"
        #base_url << "&keyword=#{tag}" if tag.present?

        response = Net::HTTP.get(URI(base_url))
        response = JSON.parse(response)
        #if response["results"].length < 1
            #try again without tags
            # oh no chinese food!
          #  base_url.gsub("&keyword=#{tag}", "")
           # response = Net::HTTP.get(URI(base_url))
           # response = JSON.parse(response)
        #end
        options = response["results"].select{|r| r["opening_hours"]} #&& r["opening_hours"]["open_now"]}
        num_results = options.length
        if num_results < 5
            choice = options.slice(0, num_results)[0 + rand(num_results)]
        elsif num_results < 1
            return {}
        else
            choice = options.slice(0, 5)[0 + rand(5)]
        end
        update_attributes!(restaurant_latitude: choice["geometry"]["location"]["lat"], restaurant_longitude: choice["geometry"]["location"]["lng"], restaurant_name: choice["name"]) if choice
        {lat: choice["geometry"]["location"]["lat"], long: choice["geometry"]["location"]["lng"], name: choice["name"], price: choice["price_level"]} if choice#, photo_reference: (choice["photos"] ? choice["photos"].first()["photo_reference"] : "")}
        {lat: nil, long: nil, name: "", price: nil} if choice.blank?
    end
end
