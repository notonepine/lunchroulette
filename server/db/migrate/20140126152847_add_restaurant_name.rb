class AddRestaurantName < ActiveRecord::Migration
  def change
    add_column :meetups, :restaurant_name, :string
  end
end
