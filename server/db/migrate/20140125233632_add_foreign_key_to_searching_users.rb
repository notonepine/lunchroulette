class AddForeignKeyToSearchingUsers < ActiveRecord::Migration
  def change
    add_column :searching_users, :user_id, :integer
  end
end
