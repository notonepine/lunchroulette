class AddMeetupIdToSearchingUser < ActiveRecord::Migration
  def change
    add_column :searching_users, :meetup_id, :integer
    add_column :searching_users, :finalized, :boolean, default: false
    change_column :searching_users, :latitude, :float
    remove_column :searching_users, :longitue
    add_column :searching_users, :longitude, :float
  end
end
