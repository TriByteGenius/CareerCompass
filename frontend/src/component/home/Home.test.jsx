import { describe, it, expect, vi } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import Home from './Home';

// Mock the navigate function from react-router-dom
const mockNavigate = vi.fn();
vi.mock('react-router-dom', async () => {
  const actual = await vi.importActual('react-router-dom');
  return {
    ...actual,
    useNavigate: () => mockNavigate
  };
});

describe('Home Component', () => {
  it('renders the home page with main sections', () => {
    renderWithProviders(<Home />);
    
    // Check that main container is rendered
    expect(screen.getByTestId('home-container')).toBeInTheDocument();
    
    // Check that all main sections are rendered
    expect(screen.getByTestId('hero-section')).toBeInTheDocument();
    expect(screen.getByTestId('features-grid')).toBeInTheDocument();
    expect(screen.getByTestId('about-section')).toBeInTheDocument();
    
    // Check main title is correct
    expect(screen.getByTestId('main-title')).toHaveTextContent('Welcome to CareerCompass');
  });

  it('displays feature cards with correct content', () => {
    renderWithProviders(<Home />);
    
    // Check features title
    expect(screen.getByTestId('features-title')).toHaveTextContent('Key Features');
    
    // Check that feature cards are rendered
    expect(screen.getByTestId('feature-card-search')).toBeInTheDocument();
    expect(screen.getByTestId('feature-card-tracking')).toBeInTheDocument();
    expect(screen.getByTestId('feature-card-interface')).toBeInTheDocument();
  });

  it('displays the About section with correct content', () => {
    renderWithProviders(<Home />);
    
    const aboutSection = screen.getByTestId('about-section');
    expect(aboutSection).toBeInTheDocument();
    expect(aboutSection).toHaveTextContent('About CareerCompass');
    expect(aboutSection).toHaveTextContent('web-based job management platform');
  });

  it('navigates to jobs page when Explore Jobs button is clicked', () => {
    renderWithProviders(<Home />);
    
    // Find and click the Explore Jobs button
    const exploreButton = screen.getByTestId('explore-jobs-button');
    fireEvent.click(exploreButton);
    
    // Check that navigation was triggered with correct path
    expect(mockNavigate).toHaveBeenCalledWith('/jobs');
  });

  it('has a working GitHub link with correct attributes', () => {
    renderWithProviders(<Home />);
    
    // Find the GitHub button
    const githubButton = screen.getByTestId('github-button');
    
    // Check that it has the correct href attribute
    expect(githubButton).toHaveAttribute('href', 'https://github.com/TriByteGenius/CareerCompass');
    
    // Check that it opens in a new tab
    expect(githubButton).toHaveAttribute('target', '_blank');
    expect(githubButton).toHaveAttribute('rel', 'noopener noreferrer');
  });

  it('displays the footer with copyright text', () => {
    renderWithProviders(<Home />);
    
    // Check that footer is rendered with correct text
    const footer = screen.getByTestId('footer-text');
    expect(footer).toBeInTheDocument();
    expect(footer).toHaveTextContent('© 2025 Developed by TriByteGenius');
  });
});