class CreateMeetups < ActiveRecord::Migration
  def change
    create_table :meetups do |t|
      t.float :latitude
      t.float :longitude
      t.float :restaurant_latitude
      t.float :restaurant_longitude

      t.timestamps
    end
  end
end
