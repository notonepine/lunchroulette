class CreateSearchingUsers < ActiveRecord::Migration
  def change
    create_table :searching_users do |t|
      t.string :name
      t.float :latitude
      t.float :longitue
      t.timestamps
    end
  end
end
