class SearchingUsersController < ApplicationController
    def create
        user = User.find_by_id(params[:id])
        #if SearchingUser.where("user_id = ?", user.id).length > 0
            #suser = 
        #end
        if user.blank?
            render status: 400, text: "User ID Not Found! Josh you fucked up."
            return
        end
        if SearchingUser.where("user_id = ?", params[:id]).length < 1
            SearchingUser.create!(name: user.name, latitude: params[:latitude], longitude: params[:longitude], user_id: user.id) 
        else
            suser = SearchingUser.where("user_id = ?", params[:id]).first
            suser.update_attributes(latitude: params[:latitude], longitude: params[:longitude], user_id: params[:id])
            suser.meetup.update_location if suser.meetup
        end
        # matches = SearchingUser.within(3, origin: suser).order('distance ASC')
        # matched = matches.all
        # ids = matched.map{|m| m.user_id}
        # options = {data: {ids: ids}}
        # if matched.length > 3
        #     search_location = SearchingUser.find_meeting_point(matched)
        #     restaurant_location = SearchingUser.find_restaurant(search_location)
        #     options[:data][:location] = restaurant_location
        #     GCM_CLIENT.send_notification([suser.user.gcm_id.to_s], options)
        #     matched.each do |match|
        #         GCM_CLIENT.send_notification([match.user.gcm_id.to_s], options)
        #         match.destroy!
        #     end
        #     suser.destroy!
        # end
        render text: "Received Search"
    end

    def test_gcm
        GCM_CLIENT.send_notification([User.find(params[:id]).gcm_id.to_s], {data: {thing: "ada", other_thing: {test: 56}}})
    end

    def search
        user = User.find_by_id(params[:id])
        suser = user.searching_user
        if suser.present? && suser.meetup
            #searching user is already member of an unfinished meetup
            final_info = suser.meetup.check_ready(true)
            if final_info
                suser.update_attribute(:finalized, true)
                suser.meetup.check_finalized
                final_info[:matches] -= [suser.user_id]
                render json: final_info
                puts "Sent #{final_info}"
                return
            else
                if suser.meetup.created_at < Time.now - 150.seconds
                    suser.meetup.searching_users.each do |u|
                        u.destroy!
                    end
                    suser.meetup.destroy!
                    suser.destroy!
                    render json: {restaurant: nil, matches: []} 
                    return
                end
                ids = suser.meetup.searching_users.map{|s| s.user_id}
                ids -= [user.id]
                render json: {restaurant: nil, matches: ids}
                puts "Sent #{{restaurant: nil, matches: ids}}"
                return
            end
        elsif suser
            #searching user is not yet in a meetup
            begin
                meetups = Meetup.within(1000, origin: suser)
                meetups = meetups.select{|m| !m.check_ready(false)}
                meetup = meetups.first
            rescue
                meetup = nil
            end
            if meetup
                suser.update_attribute(:meetup_id, meetup.id)
                meetup.update_location
            else
                new_meetup = Meetup.create!(latitude: suser.latitude, longitude: suser.longitude)
                suser.update_attribute(:meetup_id, new_meetup.id)
            end
            ids = (meetup ? meetup.searching_users.map{|s| s.user_id} - [suser.user_id] : [])
            render json: {restaurant: nil, matches: ids}
            puts "Sent #{{restaurant: nil, matches: ids}}"
            return
        else
            render json: {restaurant: nil, matches: []}
        end
    end

    def show
        SearchingUser.find_restaurant({lat: 34.017030, long: -118.497625})
    end

    def destroy
        
    end
end