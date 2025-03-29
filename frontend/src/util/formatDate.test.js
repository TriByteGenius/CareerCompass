import { describe, it, expect, vi } from 'vitest';
import formatDate from './formatDate';

describe('formatDate Utility', () => {
  // Mock the Intl.DateTimeFormat to return a consistent output for testing
  const mockDateTimeFormat = {
    format: vi.fn().mockReturnValue('Mar 28, 2025, 02:30 PM')
  };
  
  vi.spyOn(global.Intl, 'DateTimeFormat').mockImplementation(() => mockDateTimeFormat);

  it('formats a valid date string correctly', () => {
    const result = formatDate('2025-03-28T14:30:00Z');
    
    expect(global.Intl.DateTimeFormat).toHaveBeenCalledWith('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit', 
      minute: '2-digit', 
      hour12: true
    });
    
    expect(mockDateTimeFormat.format).toHaveBeenCalled();
    expect(result).toBe('Mar 28, 2025, 02:30 PM');
  });

  it('returns "Unknown date" when input is null or undefined', () => {
    expect(formatDate(null)).toBe('Unknown date');
    expect(formatDate(undefined)).toBe('Unknown date');
    expect(formatDate('')).toBe('Unknown date');
  });

  it('returns "Unknown date" when there is an error parsing the date', () => {
    // Create a scenario that would cause a Date constructor error
    vi.spyOn(global, 'Date').mockImplementationOnce(() => {
      throw new Error('Invalid date');
    });
    
    expect(formatDate('invalid-date')).toBe('Unknown date');
  });
});