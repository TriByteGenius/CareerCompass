import { describe, it, expect, vi, beforeEach } from 'vitest';
import { renderHook } from '@testing-library/react';
import { useSearchParams } from 'react-router-dom';
import { useDispatch } from 'react-redux';
import useJobFilter from './useJobFilter';
import { fetchJobs } from '../../redux/slices/jobSlice';

// Mock React Router
vi.mock('react-router-dom', () => ({
  useSearchParams: vi.fn(() => [
    {
      get: vi.fn((param) => {
        switch (param) {
          case 'page': return '2';
          case 'sortby': return 'desc';
          case 'website': return 'LINKEDIN';
          case 'keyword': return 'developer';
          case 'status': return 'new';
          case 'timeInDays': return '7';
          default: return null;
        }
      }),
      toString: vi.fn(() => 'page=2&sortby=desc')
    },
    vi.fn()
  ])
}));

// Mock Redux
vi.mock('react-redux', () => ({
  useDispatch: vi.fn()
}));

// Mock Redux action
vi.mock('../../redux/slices/jobSlice', () => ({
  fetchJobs: vi.fn()
}));

describe('useJobFilter hook', () => {
  const mockDispatch = vi.fn();
  
  beforeEach(() => {
    vi.clearAllMocks();
    useDispatch.mockReturnValue(mockDispatch);
  });

  it('dispatches fetchJobs with correct parameters on mount', () => {
    // Render the hook
    renderHook(() => useJobFilter());
    
    // Check that dispatch was called with fetchJobs
    expect(mockDispatch).toHaveBeenCalledTimes(1);
    expect(fetchJobs).toHaveBeenCalledTimes(1);
  });

  it('converts page parameter from 1-based (UI) to 0-based (API)', () => {
    // Set up mock to return page 2
    useSearchParams.mockReturnValueOnce([
      {
        get: (param) => param === 'page' ? '2' : null,
        toString: vi.fn()
      },
      vi.fn()
    ]);
    
    // Render the hook
    renderHook(() => useJobFilter());
    
    // Check that fetchJobs was called with pageNumber=1 (0-based)
    const queryString = fetchJobs.mock.calls[0][0];
    expect(queryString).toContain('pageNumber=1');
  });

  it('includes filter parameters in query string', () => {
    // Render the hook
    renderHook(() => useJobFilter());
    
    // Check that all filter parameters were included in query string
    const queryString = fetchJobs.mock.calls[0][0];
    
    // The exact format may vary, so we'll check for the presence of key parts
    expect(queryString).toContain('pageNumber=');
    expect(queryString).toContain('pageSize=');
    expect(queryString).toContain('sortBy=');
    expect(queryString).toContain('sortOrder=');
    
    // Should include our mock filter values
    expect(queryString).toContain('website=LINKEDIN');
    expect(queryString).toContain('keyword=developer');
    expect(queryString).toContain('status=new');
    expect(queryString).toContain('timeInDays=7');
  });

  it('re-fetches jobs when search params change', () => {
    // Initial render
    const { rerender } = renderHook(() => useJobFilter());
    
    // Clear the first call count
    mockDispatch.mockClear();
    fetchJobs.mockClear();
    
    // Update search params
    useSearchParams.mockReturnValueOnce([
      {
        get: (param) => {
          switch (param) {
            case 'page': return '3'; // Changed from 2 to 3
            case 'keyword': return 'engineer'; // Changed from developer to engineer
            default: return null;
          }
        },
        toString: vi.fn()
      },
      vi.fn()
    ]);
    
    // Re-render the hook
    rerender();
    
    // Should fetch jobs again with new params
    expect(mockDispatch).toHaveBeenCalledTimes(1);
    expect(fetchJobs).toHaveBeenCalledTimes(1);
  });
});