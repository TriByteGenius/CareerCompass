import { describe, it, expect, vi, beforeEach, afterEach } from 'vitest';
import getFreshness from './getFreshness';

describe('getFreshness utility', () => {
  // Save original Date implementation
  const OriginalDate = global.Date;
  
  beforeEach(() => {
    // Mock the current date to a fixed value for all tests
    const mockDate = new Date('2025-03-28T12:00:00Z');
    vi.spyOn(global, 'Date').mockImplementation((arg) => {
      if (arg === undefined) {
        return mockDate;
      }
      return new OriginalDate(arg);
    });
    global.Date.now = vi.fn(() => mockDate.getTime());
  });
  
  afterEach(() => {
    vi.restoreAllMocks();
  });

  it('returns "Just Posted" for jobs less than 24 hours old', () => {
    // 12 hours ago
    const jobDate = new Date('2025-03-28T00:00:00Z'); // 12 hours before mocked now
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('Just Posted');
    expect(result.color).toBe('#4caf50'); // Green
    expect(result.textColor).toBe('white');
  });

  it('returns "1d ago" with blue color for jobs between 24-47 hours old', () => {
    // 30 hours ago (1 day + 6 hours)
    const jobDate = new Date('2025-03-27T06:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('1d ago');
    expect(result.color).toBe('#2196f3'); // Blue
    expect(result.textColor).toBe('white');
  });

  it('returns "2d ago" with blue color for jobs between 48-71 hours old', () => {
    // 50 hours ago (2 days + 2 hours)
    const jobDate = new Date('2025-03-26T10:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('2d ago');
    expect(result.color).toBe('#2196f3'); // Blue
    expect(result.textColor).toBe('white');
  });

  it('returns "Xd ago" with orange color for jobs 3-6 days old', () => {
    // 5 days ago (120 hours)
    const jobDate = new Date('2025-03-23T12:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('5d ago');
    expect(result.color).toBe('#ff9800'); // Orange
    expect(result.textColor).toBe('white');
  });

  it('returns "Xd ago" with gray color for jobs 7+ days old', () => {
    // 10 days ago (240 hours)
    const jobDate = new Date('2025-03-18T12:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('10d ago');
    expect(result.color).toBe('#757575'); // Gray
    expect(result.textColor).toBe('white');
  });

  it('handles boundary case of exactly 47 hours correctly', () => {
    // Exactly 47 hours ago
    const jobDate = new Date('2025-03-26T13:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('1d ago');
    expect(result.color).toBe('#2196f3'); // Blue
  });

  it('handles boundary case of exactly 24 hours correctly', () => {
    // Exactly 24 hours ago
    const jobDate = new Date('2025-03-27T12:00:00Z');
    const result = getFreshness(jobDate.toISOString());
    
    expect(result.text).toBe('1d ago');
    expect(result.color).toBe('#2196f3'); // Blue
  });
});