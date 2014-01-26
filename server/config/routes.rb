Roulette::Application.routes.draw do

  match 'auth/:provider/callback', to: 'sessions#create', via: ['get']
  match 'signout', to: 'sessions#destroy', as: 'signout', via: ['get']
  resources :searching_users do
    get 'search', on: :member
    get 'test_gcm', on: :member
  end
  resources :users #do
  #   post 'post_test', on: :collection
  # end

  match '/', to: 'application#test', via: ['get']
end
