import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { motion } from 'framer-motion';
import { formatDistanceToNow } from 'date-fns';
import { ru } from 'date-fns/locale';
import { Article, Keyword } from '../types';
import { articleApi } from '../services/api';
import { LoadingState, ErrorState } from './common';
import sanitizeHtml from 'sanitize-html';
import Slider from 'react-slick';
import 'slick-carousel/slick/slick.css';
import 'slick-carousel/slick/slick-theme.css';
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
export const ArticleDetail: React.FC = () => {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [article, setArticle] = useState<Article | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [isImageLoading, setIsImageLoading] = useState(true);
  useEffect(() => {
    const fetchArticle = async () => {
      try {
        if (!id) throw new Error('Article ID is required');
        const data = await articleApi.getArticle(id);
        setArticle(data);
      } catch (err) {
        setError(err instanceof Error ? err.message : 'Failed to load article');
      } finally {
        setIsLoading(false);
      }
    };
    fetchArticle();
  }, [id]);
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
  const calculateReadingTime = (content: string): number => {
    const wordsPerMinute = 200;
    const words = content.trim().split(/\s+/).length;
    return Math.ceil(words / wordsPerMinute);
  };
  const carouselSettings = {
    dots: true,
    infinite: true,
    speed: 500,
    slidesToShow: 1,
    slidesToScroll: 1,
    autoplay: true,
    autoplaySpeed: 5000,
    adaptiveHeight: true,
    arrows: true,
    responsive: [
      {
        breakpoint: 768,
        settings: {
          arrows: false,
        }
      }
    ]
  };
  if (isLoading) return <LoadingState />;
  if (error) return <ErrorState message={error} />;
  if (!article) return <ErrorState message="Article not found" />;
  return (
    <motion.article
      initial={{ opacity: 0 }}
      animate={{ opacity: 1 }}
      exit={{ opacity: 0 }}
      style={{
        maxWidth: 'var(--max-width)',
        margin: '0 auto'
      }}
    >
      <motion.div
        initial={{ y: 20 }}
        animate={{ y: 0 }}
        transition={{ delay: 0.2 }}
        style={{
          position: 'relative',
          marginBottom: 'var(--spacing-xl)',
          padding: 'var(--spacing-xl)',
          background: 'var(--color-bg-card)',
          borderRadius: 'var(--radius-lg)'
        }}
      >
        {article.imageUrls && article.imageUrls.length > 0 && (
          <div style={{
            position: 'relative',
            width: '100%',
            paddingTop: '42%', 
            borderRadius: 'var(--radius-lg)',
            overflow: 'hidden',
            background: 'var(--color-bg-hover)',
          }}>
            {isImageLoading && (
              <div style={{
                position: 'absolute',
                top: 0,
                left: 0,
                right: 0,
                bottom: 0,
                display: 'flex',
                alignItems: 'center',
                justifyContent: 'center',
                background: 'var(--color-bg-hover)',
              }}>
                <div className="loading-spinner" />
              </div>
            )}
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
                opacity: isImageLoading ? 0 : 1,
                transition: 'opacity 0.3s ease',
              }}
              onLoad={() => setIsImageLoading(false)}
            />
            <div style={{
              position: 'absolute',
              bottom: 0,
              left: 0,
              right: 0,
              padding: 'var(--spacing-xl)',
              background: 'linear-gradient(transparent, rgba(0,0,0,0.7))',
              color: 'white',
            }}>
              <div style={{
                display: 'flex',
                gap: 'var(--spacing-md)',
                marginBottom: 'var(--spacing-md)',
                flexWrap: 'wrap',
              }}>
                <span style={{
                  background: categoryColors[article.category] || '#666',
                  color: 'white',
                  padding: '6px 16px',
                  borderRadius: 'var(--radius)',
                  fontSize: '1em',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}>
                  {categoryIcons[article.category] || '📰'} {article.category}
                </span>
                {article.sourceName && (
                  <span style={{
                    background: 'rgba(255,255,255,0.2)',
                    color: 'white',
                    padding: '6px 16px',
                    borderRadius: 'var(--radius)',
                    fontSize: '1em',
                  }}>
                    {article.sourceName}
                  </span>
                )}
              </div>
              <h1 style={{
                margin: 0,
                fontSize: '2.5em',
                lineHeight: 1.2,
                color: 'white',
                textShadow: '0 2px 4px rgba(0,0,0,0.3)',
              }}>
                {article.title}
              </h1>
              <div style={{
                display: 'flex',
                gap: 'var(--spacing-md)',
                marginTop: 'var(--spacing-md)',
                color: 'rgba(255,255,255,0.9)',
                fontSize: '0.95em',
              }}>
                <span>{formatDate(article.createdAt)}</span>
                <span>•</span>
                <span>{calculateReadingTime(article.content)} мин чтения</span>
                {article.views !== undefined && (
                  <>
                    <span>•</span>
                    <span>👁️ {article.views} просмотров</span>
                  </>
                )}
              </div>
            </div>
          </div>
        )}
        {(!article.imageUrls || article.imageUrls.length === 0) && (
          <div style={{
            padding: 'var(--spacing-xl)',
            background: 'var(--color-bg-card)',
            borderRadius: 'var(--radius-lg)',
            marginBottom: 'var(--spacing-xl)',
          }}>
            <div style={{
              display: 'flex',
              gap: 'var(--spacing-md)',
              marginBottom: 'var(--spacing-md)',
              flexWrap: 'wrap',
            }}>
              <span style={{
                background: categoryColors[article.category] || '#666',
                color: 'white',
                padding: '6px 16px',
                borderRadius: 'var(--radius)',
                fontSize: '1em',
                display: 'flex',
                alignItems: 'center',
                gap: '6px',
              }}>
                {categoryIcons[article.category] || '📰'} {article.category}
              </span>
              {article.sourceName && (
                <span style={{
                  background: 'var(--color-bg-hover)',
                  padding: '6px 16px',
                  borderRadius: 'var(--radius)',
                  fontSize: '1em',
                }}>
                  {article.sourceName}
                </span>
              )}
            </div>
            <h1 style={{
              margin: '0 0 var(--spacing-md)',
              fontSize: '2.5em',
              lineHeight: 1.2,
              color: 'var(--color-text)',
            }}>
              {article.title}
            </h1>
            <div style={{
              display: 'flex',
              gap: 'var(--spacing-md)',
              color: 'var(--color-text-light)',
              fontSize: '0.95em',
            }}>
              <span>{formatDate(article.createdAt)}</span>
              <span>•</span>
              <span>{calculateReadingTime(article.content)} мин чтения</span>
              {article.views !== undefined && (
                <>
                  <span>•</span>
                  <span>👁️ {article.views} просмотров</span>
                </>
              )}
            </div>
          </div>
        )}
        {article.keywords && article.keywords.length > 0 && (
          <div style={{
            display: 'flex',
            flexWrap: 'wrap',
            gap: 'var(--spacing-sm)',
            marginBottom: 'var(--spacing-xl)',
          }}>
            {article.keywords.filter(Boolean).map((keyword: Keyword) => (
              <span
                key={keyword.id}
                style={{
                  background: 'var(--color-bg-hover)',
                  padding: '6px 16px',
                  borderRadius: 'var(--radius)',
                  fontSize: '0.9em',
                  color: 'var(--color-text)',
                  display: 'flex',
                  alignItems: 'center',
                  gap: '6px',
                }}
              >
                {keyword.type === 'PERSON' && '👤'}
                {keyword.type === 'ORGANIZATION' && '🏢'}
                {keyword.type === 'LOCATION' && '📍'}
                {keyword.type === 'EVENT' && '📅'}
                {keyword.type === 'CONCEPT' && '💡'}
                {keyword.type === 'OTHER' && '📌'}
                {keyword.name}
              </span>
            ))}
          </div>
        )}
        <motion.div
          initial={{ opacity: 0, y: 20 }}
          animate={{ opacity: 1, y: 0 }}
          transition={{ delay: 0.4 }}
          style={{
            background: 'var(--color-bg-card)',
            padding: 'var(--spacing-xl)',
            borderRadius: 'var(--radius-lg)',
            marginBottom: 'var(--spacing-xl)',
          }}
        >
          <div 
            className="html-content"
            dangerouslySetInnerHTML={{ __html: sanitizeHtml(article.summary) }}
            style={{
              color: 'var(--color-text)',
              lineHeight: 1.8,
              fontSize: '1.2em',
              marginBottom: 'var(--spacing-xl)',
            }}
          />
          {article.imageUrls && article.imageUrls.length > 0 && (
            <motion.div
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              transition={{ delay: 0.3 }}
              style={{
                marginBottom: 'var(--spacing-xl)',
                padding: 'var(--spacing-lg)',
                background: 'var(--color-bg-card)',
                borderRadius: 'var(--radius-lg)',
              }}
            >
              <h3 style={{
                margin: '0 0 var(--spacing-lg)',
                fontSize: '1.5em',
                color: 'var(--color-text)',
              }}>
                Галерея изображений
              </h3>
              <div style={{
                maxWidth: '800px',
                margin: '0 auto',
              }}>
                <Slider {...carouselSettings}>
                  {article.imageUrls.filter(Boolean).map((imgUrl, index) => (
                    <div key={index} style={{ padding: '0 var(--spacing-sm)' }}>
                      <div style={{
                        position: 'relative',
                        paddingTop: '56.25%', 
                        borderRadius: 'var(--radius)',
                        overflow: 'hidden',
                        background: 'var(--color-bg-hover)',
                      }}>
                        <img
                          src={imgUrl}
                          alt={`Изображение ${index + 1} к статье`}
                          style={{
                            position: 'absolute',
                            top: 0,
                            left: 0,
                            width: '100%',
                            height: '100%',
                            objectFit: 'contain',
                          }}
                        />
                      </div>
                    </div>
                  ))}
                </Slider>
              </div>
            </motion.div>
          )}
          <motion.div
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            transition={{ delay: 0.6 }}
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              padding: 'var(--spacing-lg)',
              background: 'var(--color-bg-card)',
              borderRadius: 'var(--radius-lg)',
            }}
          >
            <button
              onClick={() => navigate(-1)}
              style={{
                background: 'var(--color-bg-hover)',
                border: 'none',
                padding: 'var(--spacing-md) var(--spacing-lg)',
                borderRadius: 'var(--radius)',
                color: 'var(--color-text)',
                cursor: 'pointer',
                display: 'flex',
                alignItems: 'center',
                gap: 'var(--spacing-sm)',
                fontSize: '1em',
              }}
            >
              ← Назад к списку
            </button>
            <a
              href={article.url}
              target="_blank"
              rel="noopener noreferrer"
              style={{
                background: 'var(--color-primary)',
                color: 'white',
                padding: 'var(--spacing-md) var(--spacing-lg)',
                borderRadius: 'var(--radius)',
                textDecoration: 'none',
                display: 'flex',
                alignItems: 'center',
                gap: 'var(--spacing-sm)',
                fontSize: '1em',
              }}
            >
              Читать оригинал ↗
            </a>
          </motion.div>
        </motion.div>
      </motion.div>
    </motion.article>
  );
}; 