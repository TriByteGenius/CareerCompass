import { useEffect } from "react";
import { useDispatch } from "react-redux";
import { useSearchParams } from "react-router-dom";
import { fetchJobs } from "../../redux/slices/jobSlice";

const useJobFilter = () => {
    const [searchParams] = useSearchParams();
    const dispatch = useDispatch();

    useEffect(() => {
        const params = new URLSearchParams();

        // Handle pagination (converting from 1-based UI to 0-based API)
        const currentPage = searchParams.get("page")
            ? Number(searchParams.get("page"))
            : 1;
        params.set("pageNumber", currentPage - 1);
        params.set("pageSize", "20"); // Default or from URL

        // Handle sorting
        const sortOrder = searchParams.get("sortby") || "desc";
        params.set("sortBy", "time"); // Default to time sorting
        params.set("sortOrder", sortOrder);

        // Handle filters
        const website = searchParams.get("website") || null;
        const keyword = searchParams.get("keyword") || null;
        const status = searchParams.get("status") || null;
        const timeInDays = searchParams.get("timeInDays") || null;

        if (website) {
            params.set("website", website);
        }

        if (keyword) {
            params.set("keyword", keyword);
        }

        if (status) {
            params.set("status", status);
        }

        if (timeInDays) {
            params.set("timeInDays", timeInDays);
        }

        const queryString = params.toString();
        console.log("Job filter query string:", queryString);
        
        dispatch(fetchJobs(queryString));

    }, [dispatch, searchParams]);
};

export default useJobFilter;