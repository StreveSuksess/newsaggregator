import React, { useContext, useEffect, useState } from 'react';
import { AppContext } from '../context/AppContext';
import { LoadingState, ErrorState } from './common';
import { articleApi } from '../services/api';
import {
    BarChart,
    Bar,
    XAxis,
    YAxis,
    CartesianGrid,
    Tooltip,
    Legend,
    ResponsiveContainer,
    LineChart,
    Line,
} from 'recharts';
import { formatDistanceToNow } from 'date-fns';
import { ru } from 'date-fns/locale';
import { motion } from 'framer-motion';
interface TopKeyword {
    name: string;
    count: number;
}
interface CategoryStats {
    [key: string]: number;
}
interface TrendingArticle {
    id: number;
    title: string;
    category: string;
    createdAt: string;
    views: number;
}
interface KeywordTrend {
    keyword: string;
    trends: Array<{
        date: string;
        count: number;
    }>;
}
export const Analytics: React.FC = () => {
    const { setError } = useContext(AppContext);
    const [isLoading, setIsLoading] = useState(true);
    const [error, setLocalError] = useState<string | null>(null);
    const [categoryStats, setCategoryStats] = useState<CategoryStats>({});
    const [topKeywords, setTopKeywords] = useState<TopKeyword[]>([]);
    const [trendingArticles, setTrendingArticles] = useState<TrendingArticle[]>([]);
    const [keywordTrends, setKeywordTrends] = useState<KeywordTrend[]>([]);
    const [articlesCount, setArticlesCount] = useState<number>(0);
    useEffect(() => {
        const fetchAnalytics = async () => {
            setIsLoading(true);
            try {
                const [
                    categoryStatsResponse,
                    topKeywordsResponse,
                    analyticsResponse
                ] = await Promise.all([
                    articleApi.getCategoryStats(),
                    articleApi.getTopKeywords(10),
                    articleApi.getAnalytics()
                ]);
                console.log('Category Stats Response:', categoryStatsResponse);
                console.log('Top Keywords Response:', topKeywordsResponse);
                console.log('Analytics Response:', analyticsResponse);
                const transformedCategoryStats = Object.entries(categoryStatsResponse)
                    .reduce((acc, [name, count]) => ({
                        ...acc,
                        [name]: Number(count) || 0
                    }), {} as CategoryStats);
                setCategoryStats(transformedCategoryStats);
                const transformedTopKeywords = (topKeywordsResponse as TopKeyword[])
                    .filter(k => k && k.name && typeof k.count === 'number')
                    .map(entry => ({
                        name: String(entry.name),
                        count: Number(entry.count)
                    }));
                console.log('Setting top keywords:', transformedTopKeywords);
                setTopKeywords(transformedTopKeywords);
                setTrendingArticles(analyticsResponse.trendingArticles.map((article: TrendingArticle) => ({
                    ...article,
                    views: Number(article.views) || 0
                })));
                setArticlesCount(Number(analyticsResponse.totalArticles) || 0);
                if (transformedTopKeywords.length > 0) {
                    try {
                        const keywordNames = transformedTopKeywords.map(k => k.name);
                        const trends = await articleApi.getKeywordTrends(keywordNames, 30);
                        setKeywordTrends(trends);
                    } catch (trendsError) {
                        console.warn('Failed to fetch keyword trends:', trendsError);
                        setKeywordTrends([]);
                    }
                }
            } catch (err) {
                const message = err instanceof Error ? err.message : 'Ошибка при загрузке аналитики';
                setLocalError(message);
                setError(message);
            } finally {
                setIsLoading(false);
            }
        };
        fetchAnalytics();
    }, [setError]);
    if (isLoading) {
        return <LoadingState message="Загрузка аналитики..." />;
    }
    if (error) {
        return <ErrorState message={error} onRetry={() => setLocalError(null)} />;
    }
    const categoryChartData = Object.entries(categoryStats)
        .map(([name, count]) => ({
            name: String(name),
            count: Number(count) || 0
        }))
        .filter(item => item.name && !isNaN(item.count));
    const trendChartData = (keywordTrends || []).flatMap(trend => 
        trend && trend.trends ? trend.trends.map(point => {
            if (!point) return null;
            const dateStr = (() => {
                try {
                    const date = new Date(point.date);
                    if (!isNaN(date.getTime())) {
                        return date.toISOString().split('T')[0];
                    }
                } catch {
                }
                return typeof point.date === 'string' 
                    ? point.date.split('T')[0]
                    : String(point.date);
            })();
            const count = Number(point.count);
            if (isNaN(count)) {
                console.warn(`Invalid count value for keyword ${trend.keyword}:`, point.count);
                return null;
            }
            return {
                date: dateStr,
                [String(trend.keyword)]: count 
            } as { date: string } & Record<string, number>;
        }).filter((point): point is { date: string } & Record<string, number> => point !== null) : []
    ).reduce((acc, curr) => {
        const existingPoint = acc.find(p => p.date === curr.date);
        if (existingPoint) {
            Object.entries(curr).forEach(([key, value]) => {
                if (key !== 'date') {
                    existingPoint[key] = Number(value);
                }
            });
        } else {
            acc.push({...curr});
        }
        return acc;
    }, [] as Array<{date: string} & Record<string, number>>);
    const displayTopKeywords = (topKeywords || [])
        .filter(k => k && k.name && !isNaN(k.count))
        .map(k => ({
            ...k,
            name: String(k.name),
            count: Number(k.count) || 0
        }));
    const displayTrendingArticles = (trendingArticles || [])
        .filter(a => a && a.title && !isNaN(a.views))
        .map(a => ({
            ...a,
            title: String(a.title),
            views: Number(a.views) || 0,
            createdAt: new Date(a.createdAt).toLocaleString('ru-RU')
        }));
    return (
        <div style={{ padding: 'var(--spacing-xl)' }}>
            <h1 style={{ marginBottom: 'var(--spacing-xl)' }}>Аналитика</h1>
            <div style={{ display: 'grid', gap: 'var(--spacing-xl)', gridTemplateColumns: 'repeat(auto-fit, minmax(300px, 1fr))' }}>
                {}
                <div style={{
                    background: 'var(--color-bg-card)',
                    padding: 'var(--spacing-lg)',
                    borderRadius: 'var(--radius-lg)',
                    boxShadow: 'var(--shadow-sm)'
                }}>
                    <h2 style={{ marginBottom: 'var(--spacing-lg)' }}>Общая статистика</h2>
                    <div style={{ fontSize: '2em', fontWeight: 'bold', color: 'var(--color-primary)' }}>
                        {Number(articlesCount).toLocaleString('ru-RU')}
                    </div>
                    <div style={{ color: 'var(--color-text-light)' }}>Всего статей</div>
                </div>
                {}
                <div style={{
                    background: 'var(--color-bg-card)',
                    padding: 'var(--spacing-lg)',
                    borderRadius: 'var(--radius-lg)',
                    boxShadow: 'var(--shadow-sm)',
                    gridColumn: '1 / -1'
                }}>
                    <h2 style={{ marginBottom: 'var(--spacing-lg)' }}>Статистика по категориям</h2>
                    <ResponsiveContainer width="100%" height={300}>
                        <BarChart data={categoryChartData}>
                            <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                            <XAxis
                                dataKey="name"
                                stroke="var(--color-text-light)"
                                tick={{ fill: 'var(--color-text-light)' }}
                            />
                            <YAxis
                                stroke="var(--color-text-light)"
                                tick={{ fill: 'var(--color-text-light)' }}
                            />
                            <Tooltip
                                contentStyle={{
                                    background: 'var(--color-bg-card)',
                                    border: '1px solid var(--color-border)',
                                    borderRadius: 'var(--radius)',
                                    color: 'var(--color-text)'
                                }}
                                formatter={(value: number) => [value.toLocaleString('ru-RU'), 'Количество']}
                            />
                            <Bar 
                                dataKey="count" 
                                fill="var(--color-primary)"
                                animationDuration={1500}
                            />
                        </BarChart>
                    </ResponsiveContainer>
                </div>
                {}
                <div style={{
                    background: 'var(--color-bg-card)',
                    padding: 'var(--spacing-lg)',
                    borderRadius: 'var(--radius-lg)',
                    boxShadow: 'var(--shadow-sm)',
                    width: '100%',
                    gridColumn: '1 / -1'
                }}>
                    <h2 style={{ marginBottom: 'var(--spacing-lg)' }}>Топ ключевых слов</h2>
                    {(topKeywords || []).length > 0 ? (
                        <div style={{ 
                            display: 'flex',
                            flexWrap: 'wrap',
                            gap: 'var(--spacing-md)',
                            width: '100%',
                            justifyContent: 'flex-start'
                        }}>
                            {(topKeywords || []).filter(Boolean).map((keyword) => (
                                <motion.div
                                    key={keyword.name}
                                    whileHover={{ y: -5, boxShadow: 'var(--shadow-md)' }}
                                    transition={{ duration: 0.2 }}
                                    style={{
                                        padding: 'var(--spacing-lg)',
                                        borderRadius: 'var(--radius)',
                                        background: 'var(--color-bg)',
                                        display: 'flex',
                                        flexDirection: 'column',
                                        gap: 'var(--spacing-sm)',
                                        cursor: 'pointer',
                                        minWidth: '250px',
                                        flex: '1 1 250px',
                                        maxWidth: '300px'
                                    }}
                                >
                                    <span style={{ 
                                        color: 'var(--color-text)',
                                        fontSize: '1.1em',
                                        fontWeight: '500'
                                    }}>
                                        {keyword.name}
                                    </span>
                                    <span style={{ 
                                        color: 'var(--color-primary)',
                                        fontSize: '1.2em',
                                        fontWeight: 'bold'
                                    }}>
                                        {keyword.count.toLocaleString('ru-RU')}
                                    </span>
                                </motion.div>
                            ))}
                        </div>
                    ) : (
                        <div style={{ 
                            padding: 'var(--spacing-md)',
                            color: 'var(--color-text-light)',
                            textAlign: 'center'
                        }}>
                            Нет данных о ключевых словах
                        </div>
                    )}
                </div>
                {}
                <div style={{
                    background: 'var(--color-bg-card)',
                    padding: 'var(--spacing-lg)',
                    borderRadius: 'var(--radius-lg)',
                    boxShadow: 'var(--shadow-sm)',
                    gridColumn: '1 / -1'
                }}>
                    <h2 style={{ marginBottom: 'var(--spacing-lg)' }}>Популярные статьи</h2>
                    <div style={{ display: 'grid', gap: 'var(--spacing-md)' }}>
                        {(displayTrendingArticles || []).filter(Boolean).map((article) => (
                            <div key={`article-${article.id}`} style={{
                                background: 'var(--color-bg-hover)',
                                padding: 'var(--spacing-md)',
                                borderRadius: 'var(--radius)',
                                display: 'grid',
                                gap: 'var(--spacing-sm)'
                            }}>
                                <div style={{ 
                                    display: 'flex', 
                                    justifyContent: 'space-between',
                                    alignItems: 'flex-start',
                                    gap: 'var(--spacing-md)'
                                }}>
                                    <div style={{ flex: 1 }}>
                                        <h3 style={{ 
                                            margin: 0,
                                            marginBottom: 'var(--spacing-xs)',
                                            color: 'var(--color-text)'
                                        }}>
                                            {article.title}
                                        </h3>
                                        <div style={{ 
                                            color: 'var(--color-text-light)',
                                            fontSize: '0.9em',
                                            marginBottom: 'var(--spacing-xs)'
                                        }}>
                                            {article.createdAt}
                                        </div>
                                    </div>
                                    <div style={{ 
                                        color: 'var(--color-primary)',
                                        fontWeight: 'bold',
                                        fontSize: '1.2em'
                                    }}>
                                        {article.views.toLocaleString('ru-RU')} просмотров
                                    </div>
                                </div>
                            </div>
                        ))}
                    </div>
                </div>
                {}
                {keywordTrends.length > 0 && (
                    <div style={{
                        background: 'var(--color-bg-card)',
                        padding: 'var(--spacing-lg)',
                        borderRadius: 'var(--radius-lg)',
                        boxShadow: 'var(--shadow-sm)',
                        gridColumn: '1 / -1'
                    }}>
                        <h2 style={{ marginBottom: 'var(--spacing-lg)' }}>Тренды ключевых слов</h2>
                        <ResponsiveContainer width="100%" height={400}>
                            <LineChart
                                data={trendChartData}
                                margin={{ top: 5, right: 30, left: 20, bottom: 5 }}
                            >
                                <CartesianGrid strokeDasharray="3 3" stroke="var(--color-border)" />
                                <XAxis 
                                    dataKey="date" 
                                    tickFormatter={(date) => {
                                        try {
                                            return new Date(date).toLocaleDateString('ru-RU', {
                                                month: 'short',
                                                day: 'numeric'
                                            });
                                        } catch {
                                            return String(date);
                                        }
                                    }}
                                    stroke="var(--color-text-light)"
                                    tick={{ fill: 'var(--color-text-light)' }}
                                />
                                <YAxis 
                                    stroke="var(--color-text-light)"
                                    tick={{ fill: 'var(--color-text-light)' }}
                                    tickFormatter={(value) => value.toLocaleString('ru-RU')}
                                />
                                <Tooltip
                                    contentStyle={{
                                        background: 'var(--color-bg-card)',
                                        border: '1px solid var(--color-border)',
                                        borderRadius: 'var(--radius)',
                                        color: 'var(--color-text)'
                                    }}
                                    labelFormatter={(date) => {
                                        try {
                                            return new Date(date).toLocaleDateString('ru-RU', {
                                                year: 'numeric',
                                                month: 'long',
                                                day: 'numeric'
                                            });
                                        } catch {
                                            return String(date);
                                        }
                                    }}
                                    formatter={(value: number) => [value.toLocaleString('ru-RU'), 'Количество']}
                                />
                                <Legend />
                                {(keywordTrends || []).filter(Boolean).map((trend, index) => (
                                    <Line
                                        key={`trend-${trend.keyword}`}
                                        type="monotone"
                                        dataKey={String(trend.keyword)}
                                        stroke={`hsl(${(index * 137.5) % 360}, 70%, 50%)`}
                                        strokeWidth={2}
                                        dot={false}
                                        animationDuration={1500}
                                    />
                                ))}
                            </LineChart>
                        </ResponsiveContainer>
                    </div>
                )}
            </div>
        </div>
    );
}; 