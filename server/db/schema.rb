# encoding: UTF-8
# This file is auto-generated from the current state of the database. Instead
# of editing this file, please use the migrations feature of Active Record to
# incrementally modify your database, and then regenerate this schema definition.
#
# Note that this schema.rb definition is the authoritative source for your
# database schema. If you need to create the application database on another
# system, you should be using db:schema:load, not running all the migrations
# from scratch. The latter is a flawed and unsustainable approach (the more migrations
# you'll amass, the slower it'll run and the greater likelihood for issues).
#
# It's strongly recommended that you check this file into your version control system.

ActiveRecord::Schema.define(version: 20140126152847) do

  # These are extensions that must be enabled in order to support this database
  enable_extension "plpgsql"

  create_table "meetups", force: true do |t|
    t.float    "latitude"
    t.float    "longitude"
    t.float    "restaurant_latitude"
    t.float    "restaurant_longitude"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.string   "restaurant_name"
  end

  create_table "searching_users", force: true do |t|
    t.string   "name"
    t.float    "latitude"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "user_id"
    t.integer  "matched_ids", default: [],    array: true
    t.integer  "meetup_id"
    t.boolean  "finalized",   default: false
    t.float    "longitude"
  end

  create_table "users", force: true do |t|
    t.string   "name"
    t.datetime "created_at"
    t.datetime "updated_at"
    t.integer  "gcm_id"
    t.integer  "fb_id"
  end

end
