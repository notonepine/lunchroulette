class AddGcmIdToUser < ActiveRecord::Migration
  def change
    add_column :users, :gcm_id, :integer
  end
end
