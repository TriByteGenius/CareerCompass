import React from 'react'
import Filter from './Filter'
import JobCard from './JobCard'
import Paginations from './Paginations'
import { useSelector } from 'react-redux'

const JobPage = () => {
  const loading = useSelector(state => state.job.loading);
  const { jobs, pagination } = useSelector(state => state.job);

  return (
    <div>
      <Filter />
      <div>
        {jobs && jobs.map((item, i) => <JobCard key={i} {...item} />)}
      </div>
      <Paginations numberOfPage = {pagination?.totalPages}/>
    </div>
  )
}

export default JobPage