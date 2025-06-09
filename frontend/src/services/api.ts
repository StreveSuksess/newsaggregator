import axios from 'axios';
const API_BASE_URL = '/api';
const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json',
    },
});
api.interceptors.response.use(
    response => response,
    error => {
        if (error.response) {
            console.error('API Error:', error.response.data);
            throw new Error(error.response.data.message || 'Ошибка сервера');
        } else if (error.request) {
            console.error('Network Error:', error.request);
            throw new Error('Ошибка сети. Проверьте подключение к интернету.');
        } else {
            console.error('Error:', error.message);
            throw new Error('Произошла ошибка при выполнении запроса');
        }
    }
);
export interface Article {
    id: number;
    title: string;
    content: string;
    summary: string;
    sourceUrl: string;
    publishedAt: string;
    categoryName: string;
    keywords: Keyword[];
    imageUrls: string[];
}
export interface Category {
    id: number;
    name: string;
}
export interface Keyword {
    id: number;
    name: string;
    type: string;
}
export interface ArticleFilterRequest {
    category?: string;
    keywords?: string[];
    startDate?: string;
    endDate?: string;
    page?: number;
    size?: number;
    sortBy?: string;
    sortDirection?: 'ASC' | 'DESC';
}
export interface NewsSource {
    id: number;
    name: string;
    url: string;
}
export interface ArticleResponse {
    content: Article[];
    totalPages: number;
    totalElements: number;
    size: number;
    number: number;
}
export const articleApi = {
    async getArticles(params: {
        page?: number;
        size?: number;
        sort?: string;
        search?: string;
        category?: string;
        sources?: string[];
    }): Promise<ArticleResponse> {
        const response = await fetch(`${API_BASE_URL}/articles?${new URLSearchParams({
            ...(params.page !== undefined && { page: params.page.toString() }),
            ...(params.size !== undefined && { size: params.size.toString() }),
            ...(params.sort && { sort: params.sort }),
            ...(params.search && { search: params.search }),
            ...(params.category && { category: params.category }),
            ...(params.sources && { sources: params.sources.join(',') })
        })}`);
        if (!response.ok) {
            throw new Error('Failed to fetch articles');
        }
        const data = await response.json();
        return {
            content: (data?.content || []).filter(Boolean),
            totalPages: Number(data?.totalPages) || 0,
            totalElements: Number(data?.totalElements) || 0,
            size: Number(data?.size) || 0,
            number: Number(data?.number) || 0
        };
    },
    getArticle: async (id: string) => {
        const response = await fetch(`${API_BASE_URL}/articles/${id}`);
        if (!response.ok) throw new Error('Failed to fetch article');
        const data = await response.json();
        return {
            ...data,
            keywords: (data?.keywords || []).filter(Boolean),
            imageUrls: (data?.imageUrls || []).filter(Boolean)
        };
    },
    getCategories: async () => {
        const response = await api.get('/categories');
        return (response.data || []).filter(Boolean);
    },
    getAnalytics: async () => {
        const response = await api.get('/analytics');
        const data = response.data || {};
        return {
            totalArticles: Number(data.totalArticles) || 0,
            categoryStats: (data.categoryStats || []).filter(Boolean),
            topKeywords: (data.topKeywords || []).filter(Boolean),
            trendingArticles: (data.trendingArticles || []).filter(Boolean)
        };
    },
    getCategoryStats: async () => {
        const response = await api.get('/analytics/categories/stats');
        return response.data || {};
    },
    getTopKeywords: async (limit: number = 10) => {
        const response = await api.get('/analytics/keywords/top', {
            params: { limit }
        });
        console.log('Raw top keywords response:', response.data);
        const transformedData = Array.isArray(response.data) 
            ? response.data.filter(Boolean).map((entry: any) => {
                if (!entry || typeof entry !== 'object') return null;
                const [name, count] = Object.entries(entry)[0] || ['', 0];
                return {
                    name: String(name),
                    count: Number(count) || 0
                };
            }).filter(Boolean)
            : [];
        console.log('Transformed top keywords:', transformedData);
        return transformedData;
    },
    getKeywordTrends: async (keywords: string[], days: number = 30) => {
        if (!keywords || keywords.length === 0) {
            return [];
        }
        const response = await api.get('/analytics/keywords/trends', {
            params: {
                keywords: keywords.join(','),
                days
            }
        });
        const data = response.data || {};
        const transformedData = Object.entries(data).map(([keyword, trends]) => ({
            keyword,
            trends: ((trends as Array<[string, number]>) || []).filter(Boolean).map(([date, count]) => ({
                date: new Date(date).toISOString().split('T')[0], 
                count: Number(count) || 0
            }))
        }));
        return transformedData;
    },
    async getNewsSources(): Promise<NewsSource[]> {
        const response = await fetch(`${API_BASE_URL}/sources`);
        if (!response.ok) {
            throw new Error('Failed to fetch news sources');
        }
        const data = await response.json();
        return (data || []).filter(Boolean);
    },
    async getNewsSourceNames(): Promise<string[]> {
        const response = await fetch(`${API_BASE_URL}/sources/names`);
        if (!response.ok) {
            throw new Error('Failed to fetch news source names');
        }
        const data = await response.json();
        return (data || []).filter(Boolean);
    }
};
export default api; 