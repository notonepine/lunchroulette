OmniAuth.config.logger = Rails.logger

Rails.application.config.middleware.use OmniAuth::Builder do
  provider :facebook, '239712442874755', 'b5a37e859862e50e5a99c343b5a06f3e'
end