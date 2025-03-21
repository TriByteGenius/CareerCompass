// Helper function to determine badge color and text based on freshness
const getFreshness = (timeString) => {
  if (!timeString) {
    return { color: '#e0e0e0', text: 'Unknown date' };
  }
  
  try {
    const jobDate = new Date(timeString);
    const now = new Date();
    const diffTime = Math.abs(now - jobDate);
    const diffHours = Math.floor(diffTime / (1000 * 60 * 60));
    const diffDays = Math.floor(diffHours / 24);
    
    if (diffHours < 24) {
      return { color: '#4caf50', text: 'Just Posted', textColor: 'white' }; // Green
    } else if (diffDays < 3) {
      return { color: '#2196f3', text: `${diffDays}d ago`, textColor: 'white' }; // Blue
    } else if (diffDays < 7) { 
      return { color: '#ff9800', text: `${diffDays}d ago`, textColor: 'white' }; // Orange
    } else {
      return { color: '#757575', text: `${diffDays}d ago`, textColor: 'white' }; // Gray
    }
  } catch (error) {
    return { color: '#e0e0e0', text: 'Unknown date', textColor: 'black' };
  }
};

export default getFreshness;