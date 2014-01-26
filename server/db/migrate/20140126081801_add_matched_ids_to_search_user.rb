class AddMatchedIdsToSearchUser < ActiveRecord::Migration
  def change
    add_column :searching_users, :matched_ids, :integer, array: :true, default: []
  end
end
