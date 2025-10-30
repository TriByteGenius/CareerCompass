import axios from 'axios';

const api = axios.create({
    baseURL: '/api',
    withCredentials: true,
})

export default api;

// AI analyze by job URL
export async function analyzeJobByUrl(url) {
    // Backend endpoint: /api/jobs/ai/analyze-url
    const { data } = await api.post('/jobs/ai/analyze-url', { url });
    return data; // expected to be the AIJobAnalysisResponse object
}