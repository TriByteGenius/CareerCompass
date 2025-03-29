import { describe, it, expect } from 'vitest';
import isJobFavorited from './isJobFavorited';

describe('isJobFavorited Utility', () => {
  const mockFavoriteJobs = [
    { job: { id: '1' } },
    { job: { id: '2' } },
    { job: { id: '3' } }
  ];

  it('returns true when job is in favorites', () => {
    expect(isJobFavorited(mockFavoriteJobs, '1')).toBe(true);
    expect(isJobFavorited(mockFavoriteJobs, '2')).toBe(true);
    expect(isJobFavorited(mockFavoriteJobs, '3')).toBe(true);
  });

  it('returns false when job is not in favorites', () => {
    expect(isJobFavorited(mockFavoriteJobs, '4')).toBe(false);
    expect(isJobFavorited(mockFavoriteJobs, '999')).toBe(false);
  });

  it('returns false when favorite jobs array is empty', () => {
    expect(isJobFavorited([], '1')).toBe(false);
  });

  it('returns false when favorite jobs is null or undefined', () => {
    expect(isJobFavorited(null, '1')).toBe(false);
    expect(isJobFavorited(undefined, '1')).toBe(false);
  });

  it('returns false when job ID is null or undefined', () => {
    expect(isJobFavorited(mockFavoriteJobs, null)).toBe(false);
    expect(isJobFavorited(mockFavoriteJobs, undefined)).toBe(false);
    expect(isJobFavorited(mockFavoriteJobs, '')).toBe(false);
  });

  it('handles favorite jobs with missing job property', () => {
    const invalidFavorites = [
      { id: '1' }, // Missing job property
      { job: null }, // Null job
      { job: { id: '3' } } // Valid job
    ];
    
    expect(isJobFavorited(invalidFavorites, '1')).toBe(false);
    expect(isJobFavorited(invalidFavorites, '3')).toBe(true);
  });
});