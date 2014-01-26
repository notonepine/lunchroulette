class UsersController < ApplicationController
    def create
        fb_params = (params[:user].class == String ? JSON.parse(params[:user]) : params[:user])
        user = User.where('fb_id = ?', fb_params["id"]).first
        if user.blank?
             new_user = User.create!(name: fb_params["name"], gcm_id: fb_params["GCM_ID"], fb_id: fb_params["id"])
        else
            user.update_attributes!(name: fb_params["name"], gcm_id: fb_params["GCM_ID"], fb_id: fb_params["id"])
            new_user = user
        end
        render json: {id: new_user.id, new: user.blank?}
    end

    def update
        User.update_attributes!(params[:user])
    end

    # def post_test
    #     binding.pry
    # end

    def show
        user = User.find(params[:id].to_i)
        if user
            render json: {name: user.name.split(" ").first, fb_id: user.fb_id, id: user.id}
        else
            render json: {name: "Invalid", fb_id: nil, id: nil}
        end
    end

    def destroy
        User.find(params[:id]).destroy!
    end
end
