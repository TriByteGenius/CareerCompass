import { describe, it, expect, vi } from 'vitest';
import { screen, fireEvent } from '@testing-library/react';
import { renderWithProviders } from '../../util/test-utils';
import StatusFilter from './StatusFilter';

// Mock useMediaQuery hook from MUI
vi.mock('@mui/material', async () => {
  const actual = await vi.importActual('@mui/material');
  return {
    ...actual,
    useMediaQuery: () => false // Always return desktop layout
  };
});

describe('StatusFilter Component', () => {
  it('renders all status tabs', () => {
    const onChange = vi.fn();
    renderWithProviders(<StatusFilter value="all" onChange={onChange} />);
    
    // Verify that all status tabs are rendered
    expect(screen.getByTestId('status-tab-all')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-new')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-applied')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-interview')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-offer')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-rejected')).toBeInTheDocument();
  });

  it('calls onChange handler when a tab is clicked', () => {
    const handleChange = vi.fn();
    renderWithProviders(<StatusFilter value="all" onChange={handleChange} />);
    
    // Find and click on the "applied" tab
    const appliedTab = screen.getByTestId('status-tab-applied');
    fireEvent.click(appliedTab);
    
    // Verify that onChange was called
    expect(handleChange).toHaveBeenCalled();
  });

  it('displays badge counts when provided', () => {
    const counts = {
      all: 10,
      new: 5,
      applied: 3
    };
    
    renderWithProviders(
      <StatusFilter value="all" onChange={vi.fn()} counts={counts} />
    );
    
    expect(screen.getByTestId('badge-count-all')).toBeInTheDocument();
    expect(screen.getByTestId('badge-count-new')).toBeInTheDocument();
    expect(screen.getByTestId('badge-count-applied')).toBeInTheDocument();
    
    // Badges should not exist for tabs with no counts
    expect(screen.queryByTestId('badge-count-interview')).not.toBeInTheDocument();
    expect(screen.queryByTestId('badge-count-offer')).not.toBeInTheDocument();
    expect(screen.queryByTestId('badge-count-rejected')).not.toBeInTheDocument();
  });

  it('renders correctly with empty counts object', () => {
    renderWithProviders(<StatusFilter value="all" onChange={vi.fn()} counts={{}} />);
    
    // All tabs should be rendered
    expect(screen.getByTestId('status-tab-all')).toBeInTheDocument();
    expect(screen.getByTestId('status-tab-new')).toBeInTheDocument();
    
    // No badges should be rendered
    expect(screen.queryByTestId('badge-count-all')).not.toBeInTheDocument();
    expect(screen.queryByTestId('badge-count-new')).not.toBeInTheDocument();
  });
});