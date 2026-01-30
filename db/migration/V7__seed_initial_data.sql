-- Migration: Seed initial data
-- Insert default topics and website configuration

-- Insert initial programming topics
INSERT INTO topics (tpname, description) VALUES
('jQuery', 'A fast, small, and feature-rich JavaScript library'),
('PHP', 'A popular general-purpose scripting language for web development'),
('JavaScript', 'The programming language of the web'),
('Bootstrap', 'The most popular CSS Framework for responsive web development');

-- Insert default website configuration
INSERT INTO website_data (title, description) VALUES
('Minded', 'Community-driven Q&A platform for knowledge sharing on programming topics');

-- Note: Admin user will be created through the application
-- Default admin credentials should not be hardcoded in migrations for security
