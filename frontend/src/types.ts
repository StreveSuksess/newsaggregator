export interface Article {
  id: string;
  title: string;
  summary: string;
  content: string;
  category: string;
  keywords: Keyword[];
  createdAt: string;
  imageUrls?: string[];
  views?: number;
  sourceName?: string;
  readingTime?: number;
  isTrending?: boolean;
  url: string;
}
export interface ArticleResponse {
  content: Article[];
  totalPages: number;
  totalElements: number;
  size: number;
  number: number;
}
export interface Category {
  id: string;
  name: string;
  color?: string; 
  icon?: string; 
}
export interface Keyword {
  id: string;
  name: string;
  type: string;
}
export interface AnalyticsData {
  totalArticles: number;
  categoryStats: {
    name: string;
    count: number;
  }[];
  topKeywords: {
    id: string;
    name: string;
    count: number;
  }[];
  trendingArticles: {
    id: number;
    title: string;
    category: string;
    createdAt: string;
    views: number;
  }[];
}
export interface NewsSource {
  id: number;
  name: string;
}
export interface ArticleFilter {
  search?: string;
  category?: string;
  keywords?: string[];
  sources?: string[];
  startDate?: string;
  endDate?: string;
  sortBy?: 'createdAt' | 'views' | 'relevance';
  sortDirection?: 'asc' | 'desc';
  page?: number;
  size?: number;
}
