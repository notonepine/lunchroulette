namespace :db do
  desc "TODO"
  task delete_stuff: :environment do
    Meetup.destroy_all
    SearchingUser.destroy_all
  end

end
