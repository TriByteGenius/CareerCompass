const isJobFavorited = (favoriteJobs = [], jobId) => {
  if (!Array.isArray(favoriteJobs) || !jobId) return false;
  return favoriteJobs.some(item => item.job?.id === jobId);
};

export default isJobFavorited;