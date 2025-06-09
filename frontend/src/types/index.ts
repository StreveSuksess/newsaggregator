export interface Article {
  id: number;
  title: string;
  summary: string;
  content: string;
  sourceUrl: string;
  publishedAt: string;
  category: Category;
  keywords: Keyword[];
  imageUrls: string[];
  views?: number;
}
export interface Category {
  id: string;
  name: string;
}
export interface Keyword {
  id: string;
  name: string;
  count?: number;
}
export interface TrendingArticle {
  id: number;
  title: string;
  category: string;
  createdAt: string;
  views: number;
}
export interface AnalyticsData {
  totalArticles: number;
  totalViews: number;
  categoryStats: CategoryStats[];
  topKeywords: Keyword[];
  keywordTrends: KeywordTrend[];
  trendingArticles: TrendingArticle[];
}
export interface CategoryStats {
  name: string;
  count: number;
}
export interface KeywordTrend {
  keyword: string;
  trends: Array<{
    date: string | Date;
    count: number;
  }>;
} 