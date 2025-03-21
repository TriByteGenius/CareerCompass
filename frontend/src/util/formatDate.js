// Format date for display
const formatDate = (timeString) => {
  if (!timeString) return 'Unknown date';
  
  try {
    const date = new Date(timeString);
    return new Intl.DateTimeFormat('en-US', {
      month: 'short',
      day: 'numeric',
      year: 'numeric',
      hour: '2-digit', 
      minute: '2-digit', 
      hour12: true
    }).format(date);
  } catch (error) {
    return 'Unknown date';
  }
};

export default formatDate;