import { describe, it, expect, vi, beforeEach } from 'vitest';
import { screen } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import JobPage from './JobPage';

// Mock the useJobFilter hook
vi.mock('../hooks/useJobFilter', () => ({
  default: vi.fn()
}));

// Mock the JobCard component
vi.mock('./JobCard', () => ({
  default: (props) => (
    <div data-testid={`job-card-${props.id}`}>
      <div data-testid="job-title">{props.name}</div>
      <div data-testid="job-company">{props.company}</div>
    </div>
  )
}));

// Mock the Filter component
vi.mock('./Filter', () => ({
  default: () => <div data-testid="filter-component">Filter Component</div>
}));

// Mock the Paginations component
vi.mock('./Paginations', () => ({
  default: (props) => <div data-testid="pagination-component">Pagination: {props.numberOfPage} pages</div>
}));

describe('JobPage Component', () => {
  beforeEach(() => {
    vi.clearAllMocks();
  });

  it('renders the job page container with title', () => {
    // Set up initial state with empty jobs
    const initialState = {
      job: {
        jobs: [],
        loading: false,
        error: null,
        pagination: {
          pageNumber: 0,
          pageSize: 20,
          totalElements: 0,
          totalPages: 0
        }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: initialState });
    
    // Check that the job listing title is rendered
    expect(screen.getByText('Job Listings')).toBeInTheDocument();
  });

  it('renders the filter component', () => {
    // Set up initial state
    const initialState = {
      job: {
        jobs: [],
        loading: false,
        error: null,
        pagination: { totalPages: 0 }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: initialState });
    
    // Check that the filter component is rendered
    expect(screen.getByTestId('filter-component')).toBeInTheDocument();
  });

  it('shows loading indicator when loading jobs', () => {
    // Set up loading state
    const loadingState = {
      job: {
        jobs: [],
        loading: true,
        error: null,
        pagination: { totalPages: 0 }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: loadingState });
    
    // Check that loading indicator is shown
    const loadingIndicator = screen.getByRole('progressbar');
    expect(loadingIndicator).toBeInTheDocument();
  });

  it('renders job cards when jobs are available', () => {
    // Sample job data
    const jobsData = [
      {
        id: '1',
        name: 'Frontend Developer',
        company: 'Tech Inc',
        type: 'Full-time',
        location: 'Remote',
        time: '2025-03-20T12:00:00Z',
        url: 'https://example.com/job1',
        website: 'LINKEDIN'
      },
      {
        id: '2',
        name: 'Backend Developer',
        company: 'Code Corp',
        type: 'Full-time',
        location: 'New York',
        time: '2025-03-25T12:00:00Z',
        url: 'https://example.com/job2',
        website: 'INDEED'
      }
    ];

    // Set up state with jobs
    const stateWithJobs = {
      job: {
        jobs: jobsData,
        loading: false,
        error: null,
        pagination: {
          pageNumber: 0,
          pageSize: 20,
          totalElements: 2,
          totalPages: 1
        }
      },
      auth: { isAuthenticated: false },
      favorites: { favoriteJobs: [] }
    };

    renderWithProviders(<JobPage />, { preloadedState: stateWithJobs });
    
    // Check that job count is displayed
    expect(screen.getByText('Found 2 jobs')).toBeInTheDocument();
    
    // Check that job cards are rendered
    expect(screen.getByTestId('job-card-1')).toBeInTheDocument();
    expect(screen.getByTestId('job-card-2')).toBeInTheDocument();
  });

  it('shows pagination when there are multiple pages', () => {
    // Set up state with multiple pages
    const stateWithPagination = {
      job: {
        jobs: [],
        loading: false,
        error: null,
        pagination: {
          pageNumber: 0,
          pageSize: 20,
          totalElements: 100,
          totalPages: 5
        }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: stateWithPagination });
    
    // Check that pagination is rendered
    expect(screen.getByTestId('pagination-component')).toBeInTheDocument();
    expect(screen.getByText('Pagination: 5 pages')).toBeInTheDocument();
  });

  it('shows no jobs message when jobs array is empty', () => {
    // Set up state with no jobs
    const emptyState = {
      job: {
        jobs: [],
        loading: false,
        error: null,
        pagination: { totalPages: 0 }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: emptyState });
    
    // Check that no jobs message is shown
    expect(screen.getByText(/No jobs found/i)).toBeInTheDocument();
  });

  it('shows error message when there is an error', () => {
    // Set up state with error
    const errorState = {
      job: {
        jobs: [],
        loading: false,
        error: 'Failed to fetch jobs',
        pagination: { totalPages: 0 }
      }
    };

    renderWithProviders(<JobPage />, { preloadedState: errorState });
    
    // Check that error message is shown
    expect(screen.getByText('Failed to fetch jobs')).toBeInTheDocument();
  });
});