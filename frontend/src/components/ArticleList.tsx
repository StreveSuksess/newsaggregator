import React, { useContext, useEffect, useState } from 'react';
import { useSearchParams, useNavigate } from 'react-router-dom';
import { AppContext } from '../context/AppContext';
import { LoadingState, ErrorState } from './common';
import { Article as FrontendArticle, Category, Keyword } from '../types';
import { articleApi, Article as ApiArticle } from '../services/api';
import { formatDistanceToNow } from 'date-fns';
import { ru } from 'date-fns/locale';
import { motion, AnimatePresence } from 'framer-motion';
const categoryIcons: Record<string, string> = {
  'Политика': '🏛️',
  'Экономика': '💰',
  'Общество': '👥',
  'Наука': '🔬',
  'Технологии': '💻',
  'Спорт': '⚽',
  'Культура': '🎭',
  'Происшествия': '🚨',
  'Здоровье': '🏥',
  'Образование': '📚'
};
const categoryColors: Record<string, string> = {
  'Политика': '#FF6B6B',
  'Экономика': '#4ECDC4',
  'Общество': '#45B7D1',
  'Наука': '#96CEB4',
  'Технологии': '#FFEEAD',
  'Спорт': '#D4A5A5',
  'Культура': '#9B59B6',
  'Происшествия': '#E74C3C',
  'Здоровье': '#2ECC71',
  'Образование': '#3498DB'
};
const convertApiArticleToFrontendArticle = (apiArticle: ApiArticle): FrontendArticle => {
  return {
    ...apiArticle,
    id: apiArticle.id.toString(),
    createdAt: apiArticle.publishedAt,
    url: apiArticle.sourceUrl,
    category: apiArticle.categoryName ?? 'Без категории',
    keywords: (apiArticle.keywords || []).filter(Boolean).map(k => ({
      ...k,
      id: k.id.toString()
    })),
    imageUrls: apiArticle.imageUrls || [],
    views: 0
  };
};
export const ArticleList: React.FC = () => {
  const { setError } = useContext(AppContext);
  const [searchParams, setSearchParams] = useSearchParams();
  const [articles, setArticles] = useState<FrontendArticle[]>([]);
  const [categories, setCategories] = useState<Category[]>([]);
  const [sources, setSources] = useState<string[]>([]);
  const [totalPages, setTotalPages] = useState(0);
  const [isLoadingArticles, setIsLoadingArticles] = useState(false);
  const [isLoadingCategories, setIsLoadingCategories] = useState(false);
  const [isLoadingSources, setIsLoadingSources] = useState(false);
  const [error, setLocalError] = useState<string | null>(null);
  const navigate = useNavigate();
  const [hoveredArticle, setHoveredArticle] = useState<string | null>(null);
  const page = Number(searchParams.get('page')) || 0;
  const size = Number(searchParams.get('size')) || 10;
  const sort = searchParams.get('sort') || 'createdAt,desc';
  const search = searchParams.get('search') || '';
  const category = searchParams.get('category') || '';
  const source = searchParams.get('source') || '';
  useEffect(() => {
    let isMounted = true;
    const fetchArticles = async () => {
      setIsLoadingArticles(true);
      try {
        const data = await articleApi.getArticles({ 
          page, 
          size, 
          sort, 
          search, 
          category,
          sources: source ? [source] : undefined 
        });
        console.log('Received articles data:', JSON.stringify(data, null, 2));
        if (isMounted) {
          const convertedArticles = (data?.content || []).map(article => {
            if (!article) return null;
            console.log('Converting article:', JSON.stringify(article, null, 2));
            return convertApiArticleToFrontendArticle(article);
          }).filter(Boolean) as FrontendArticle[];
          setArticles(convertedArticles);
          setTotalPages(data?.totalPages || 0);
        }
      } catch (err) {
        if (isMounted) {
          const message = err instanceof Error ? err.message : 'Ошибка при загрузке статей';
          setLocalError(message);
          setError(message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingArticles(false);
        }
      }
    };
    const fetchCategories = async () => {
      setIsLoadingCategories(true);
      try {
        const data = await articleApi.getCategories();
        if (isMounted) {
          setCategories(data || []);
        }
      } catch (err) {
        if (isMounted) {
          const message = err instanceof Error ? err.message : 'Ошибка при загрузке категорий';
          setLocalError(message);
          setError(message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingCategories(false);
        }
      }
    };
    const fetchSources = async () => {
      setIsLoadingSources(true);
      try {
        const data = await articleApi.getNewsSourceNames();
        if (isMounted) {
          setSources(data || []);
        }
      } catch (err) {
        if (isMounted) {
          const message = err instanceof Error ? err.message : 'Ошибка при загрузке источников';
          setLocalError(message);
          setError(message);
        }
      } finally {
        if (isMounted) {
          setIsLoadingSources(false);
        }
      }
    };
    fetchArticles();
    fetchCategories();
    fetchSources();
    return () => {
      isMounted = false;
    };
  }, [page, size, sort, search, category, source, setError]);
  const handleSearch = (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    const formData = new FormData(e.currentTarget);
    const searchValue = formData.get('search') as string;
    setSearchParams({ ...Object.fromEntries(searchParams), search: searchValue, page: '0' });
  };
  const handleCategoryChange = (categoryId: string) => {
    setSearchParams({ ...Object.fromEntries(searchParams), category: categoryId, page: '0' });
  };
  const handleSourceChange = (sourceName: string) => {
    setSearchParams({ ...Object.fromEntries(searchParams), source: sourceName, page: '0' });
  };
  const handleResetFilters = () => {
    setSearchParams({ 
      ...Object.fromEntries(searchParams),
      category: '',
      source: '',
      page: '0'
    });
  };
  const handleSortChange = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setSearchParams({ ...Object.fromEntries(searchParams), sort: e.target.value, page: '0' });
  };
  const calculateReadingTime = (content: string): number => {
    const wordsPerMinute = 200;
    const words = content.trim().split(/\s+/).length;
    return Math.ceil(words / wordsPerMinute);
  };
  const formatDate = (dateString: string): string => {
    try {
      return formatDistanceToNow(new Date(dateString), { 
        addSuffix: true,
        locale: ru 
      });
    } catch (e) {
      return 'Недавно';
    }
  };
  if (isLoadingArticles || isLoadingCategories || isLoadingSources) {
    return <LoadingState message="Загрузка статей..." />;
  }
  if (error) {
    return <ErrorState message={error} onRetry={() => setLocalError(null)} />;
  }
  return (
    <div className="animate-fade-in" style={{ maxWidth: 'var(--max-width)', margin: '0 auto' }}>
      <div className="glass" style={{ padding: 'var(--spacing-lg)', borderRadius: 'var(--radius)', marginBottom: 'var(--spacing-lg)' }}>
        <form onSubmit={handleSearch} style={{ display: 'flex', gap: 'var(--spacing-md)', marginBottom: 'var(--spacing-md)' }}>
          <input
            type="text"
            name="search"
            placeholder="Поиск статей..."
            defaultValue={search}
            className="glass"
            style={{
              flex: 1,
              padding: 'var(--spacing-md)',
              borderRadius: 'var(--radius)',
              border: '1px solid var(--color-border)',
              background: 'var(--color-bg-input)',
              color: 'var(--color-text)',
              fontSize: '1rem',
            }}
          />
          <button
            type="submit"
            className="glass glass-hover"
            style={{
              padding: 'var(--spacing-md) var(--spacing-lg)',
              background: 'var(--color-primary)',
              color: 'var(--color-text-inverse)',
              border: 'none',
              borderRadius: 'var(--radius)',
              cursor: 'pointer',
              fontSize: '1rem',
              fontWeight: 500,
            }}
          >
            Поиск
          </button>
        </form>
        <div style={{ display: 'flex', gap: 'var(--spacing-md)', flexWrap: 'wrap', marginBottom: 'var(--spacing-md)' }}>
          {categories && categories.length > 0 && (
            <div style={{ display: 'flex', gap: 'var(--spacing-md)', flexWrap: 'wrap', alignItems: 'center' }}>
              {(categories || []).filter(Boolean).map((cat) => (
                <button
                  key={cat.id}
                  onClick={() => handleCategoryChange(cat.id.toString())}
                  className={`glass glass-hover ${category === cat.id.toString() ? 'active' : ''}`}
                  style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    background: category === cat.id.toString() ? 'var(--color-primary)' : 'var(--color-bg-card)',
                    color: category === cat.id.toString() ? 'var(--color-text-inverse)' : 'var(--color-text)',
                    border: '1px solid var(--color-border)',
                    borderRadius: 'var(--radius)',
                    cursor: 'pointer',
                    fontSize: '0.9rem',
                  }}
                >
                  {cat.name}
                </button>
              ))}
              {(category || source) && (
                <button
                  onClick={handleResetFilters}
                  className="glass glass-hover"
                  style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    background: 'var(--color-bg-card)',
                    color: 'var(--color-text)',
                    border: '1px solid var(--color-border)',
                    borderRadius: 'var(--radius)',
                    cursor: 'pointer',
                    fontSize: '0.9rem',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 'var(--spacing-sm)'
                  }}
                >
                  <span>🔄</span> Сбросить фильтры
                </button>
              )}
            </div>
          )}
          {sources && sources.length > 0 && (
            <div style={{ display: 'flex', gap: 'var(--spacing-md)', flexWrap: 'wrap' }}>
              {(sources || []).filter(Boolean).map((sourceName) => (
                <button
                  key={sourceName}
                  onClick={() => handleSourceChange(sourceName)}
                  className={`glass glass-hover ${source === sourceName ? 'active' : ''}`}
                  style={{
                    padding: 'var(--spacing-sm) var(--spacing-md)',
                    background: source === sourceName ? 'var(--color-primary)' : 'var(--color-bg-card)',
                    color: source === sourceName ? 'var(--color-text-inverse)' : 'var(--color-text)',
                    border: '1px solid var(--color-border)',
                    borderRadius: 'var(--radius)',
                    cursor: 'pointer',
                    fontSize: '0.9rem',
                  }}
                >
                  📰 {sourceName}
                </button>
              ))}
            </div>
          )}
        </div>
        <select
          value={sort}
          onChange={handleSortChange}
          className="glass"
          style={{
            padding: 'var(--spacing-md)',
            borderRadius: 'var(--radius)',
            border: '1px solid var(--color-border)',
            background: 'var(--color-bg-input)',
            color: 'var(--color-text)',
            fontSize: '0.9rem',
            cursor: 'pointer',
          }}
        >
          <option value="createdAt,desc">Сначала новые</option>
          <option value="createdAt,asc">Сначала старые</option>
          <option value="title,asc">По названию (А-Я)</option>
          <option value="title,desc">По названию (Я-А)</option>
        </select>
      </div>
      <div className="article-grid" style={{
        display: 'grid',
        gridTemplateColumns: 'repeat(auto-fill, minmax(300px, 1fr))',
        gap: 'var(--spacing-lg)',
        margin: '0 auto'
      }}>
        <AnimatePresence>
          {(articles || []).filter(Boolean).map((article) => (
            <motion.article
              key={article.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: -20 }}
              whileHover={{ y: -5 }}
              transition={{ duration: 0.2 }}
              onHoverStart={() => setHoveredArticle(article.id)}
              onHoverEnd={() => setHoveredArticle(null)}
              onClick={() => navigate(`/article/${article.id}`)}
              style={{
                background: 'var(--color-bg-card)',
                borderRadius: 'var(--radius-lg)',
                overflow: 'hidden',
                cursor: 'pointer',
                boxShadow: '0 4px 6px rgba(0, 0, 0, 0.1)',
                position: 'relative',
                height: '100%',
                display: 'flex',
                flexDirection: 'column'
              }}
            >
              {article.imageUrls && article.imageUrls.length > 0 && (
                <div style={{
                  position: 'relative',
                  paddingTop: '56.25%', 
                  overflow: 'hidden'
                }}>
                  <img
                    src={article.imageUrls[0]}
                    alt={article.title}
                    style={{
                      position: 'absolute',
                      top: 0,
                      left: 0,
                      width: '100%',
                      height: '100%',
                      objectFit: 'cover',
                      transition: 'transform 0.3s ease'
                    }}
                  />
                  {article.isTrending && (
                    <div style={{
                      position: 'absolute',
                      top: 'var(--spacing-sm)',
                      right: 'var(--spacing-sm)',
                      background: 'rgba(255, 107, 107, 0.9)',
                      color: 'white',
                      padding: '4px 8px',
                      borderRadius: 'var(--radius)',
                      fontSize: '0.8em',
                      fontWeight: 'bold'
                    }}>
                      🔥 Популярное
                    </div>
                  )}
                </div>
              )}
              <div style={{
                padding: 'var(--spacing-lg)',
                flex: 1,
                display: 'flex',
                flexDirection: 'column',
                gap: 'var(--spacing-md)'
              }}>
                <div style={{
                  display: 'flex',
                  alignItems: 'center',
                  gap: 'var(--spacing-sm)'
                }}>
                  <span style={{
                    background: categoryColors[article.category] || '#666',
                    color: 'white',
                    padding: '4px 12px',
                    borderRadius: 'var(--radius)',
                    fontSize: '0.9em',
                    display: 'flex',
                    alignItems: 'center',
                    gap: '4px'
                  }}>
                    {categoryIcons[article.category] || '📰'} {article.category}
                  </span>
                  {article.sourceName && (
                    <span style={{
                      color: 'var(--color-text-light)',
                      fontSize: '0.9em'
                    }}>
                      {article.sourceName}
                    </span>
                  )}
                </div>
                <div style={{
                  display: 'flex',
                  flexDirection: 'column',
                  gap: 'var(--spacing-md)'
                }}>
                  <h2 style={{
                    margin: 0,
                    fontSize: '1.4em',
                    lineHeight: 1.4,
                    color: 'var(--color-text)',
                    overflow: 'hidden',
                    textOverflow: 'ellipsis',
                    display: '-webkit-box',
                    WebkitLineClamp: 2,
                    WebkitBoxOrient: 'vertical'
                  }}>
                    {article.title}
                  </h2>
                  <p style={{
                    color: 'var(--color-text-light)',
                    fontSize: '0.95em',
                    lineHeight: 1.6,
                    display: '-webkit-box',
                    WebkitLineClamp: 3,
                    WebkitBoxOrient: 'vertical',
                    overflow: 'hidden',
                    margin: 0
                  }}>
                    {article.summary}
                  </p>
                </div>
                <div style={{
                  marginTop: 'auto',
                  display: 'flex',
                  justifyContent: 'space-between',
                  alignItems: 'center',
                  color: 'var(--color-text-light)',
                  fontSize: '0.9em'
                }}>
                  <div style={{ display: 'flex', gap: 'var(--spacing-md)' }}>
                    <span>{formatDate(article.createdAt)}</span>
                    <span>•</span>
                    <span>{article.readingTime || calculateReadingTime(article.content)} мин</span>
                  </div>
                  {article.views !== undefined && (
                    <span>👁️ {article.views}</span>
                  )}
                </div>
                {article.keywords && article.keywords.length > 0 && (
                  <div style={{
                    display: 'flex',
                    flexWrap: 'wrap',
                    gap: 'var(--spacing-xs)',
                    marginTop: 'var(--spacing-sm)'
                  }}>
                    {(article.keywords || []).filter(Boolean).slice(0, 3).map((keyword) => (
                      <span
                        key={keyword.id}
                        style={{
                          background: 'var(--color-bg-hover)',
                          padding: '2px 8px',
                          borderRadius: 'var(--radius)',
                          fontSize: '0.8em',
                          color: 'var(--color-text-light)'
                        }}
                      >
                        {keyword.name}
                      </span>
                    ))}
                    {(article.keywords || []).length > 3 && (
                      <span style={{
                        background: 'var(--color-bg-hover)',
                        padding: '2px 8px',
                        borderRadius: 'var(--radius)',
                        fontSize: '0.8em',
                        color: 'var(--color-text-light)'
                      }}>
                        +{(article.keywords || []).length - 3}
                      </span>
                    )}
                  </div>
                )}
              </div>
            </motion.article>
          ))}
        </AnimatePresence>
      </div>
      {totalPages > 1 && (
        <div style={{ 
          display: 'flex', 
          justifyContent: 'center', 
          gap: 'var(--spacing-sm)', 
          marginTop: 'var(--spacing-lg)',
        }}>
          {Array.from({ length: Number(totalPages) || 0 }, (_, i) => (
            <button
              key={i}
              onClick={() => setSearchParams({ ...Object.fromEntries(searchParams), page: i.toString() })}
              className={`glass glass-hover ${page === i ? 'active' : ''}`}
              style={{
                padding: 'var(--spacing-sm) var(--spacing-md)',
                background: page === i ? 'var(--color-primary)' : 'var(--color-bg-card)',
                color: page === i ? 'var(--color-text-inverse)' : 'var(--color-text)',
                border: '1px solid var(--color-border)',
                borderRadius: 'var(--radius)',
                cursor: 'pointer',
                minWidth: '40px',
              }}
            >
              {i + 1}
            </button>
          ))}
        </div>
      )}
    </div>
  );
}; 