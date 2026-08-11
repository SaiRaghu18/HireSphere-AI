import React, { useState } from 'react';

export default function RecruiterAssistant({ recruiterJobs, token, API_BASE, onViewCandidate }) {
  const [selectedJobId, setSelectedJobId] = useState(
    recruiterJobs && recruiterJobs.length > 0 ? recruiterJobs[0].id : ''
  );
  const [question, setQuestion] = useState('');
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');
  const [response, setResponse] = useState(null);

  const sampleQuestions = [
    "Find the best 5 candidates",
    "Which candidates have strong Java experience?",
    "Who is missing React?",
    "Show candidates with Spring Boot experience",
    "Which candidates have the highest match scores?"
  ];

  const handleAskAssistant = async (questionToAsk) => {
    const queryText = questionToAsk || question;
    if (!selectedJobId) {
      alert("Please select a job first.");
      return;
    }
    if (!queryText || !queryText.trim()) {
      alert("Please enter or select a question.");
      return;
    }

    setLoading(true);
    setError('');
    setResponse(null);

    try {
      const res = await fetch(`${API_BASE}/recruiter/assistant`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          jobId: Number(selectedJobId),
          question: queryText.trim()
        })
      });

      if (res.ok) {
        const data = await res.json();
        setResponse(data);
      } else {
        const errData = await res.json().catch(() => ({}));
        setError(errData.message || "Failed to get AI assistant response");
      }
    } catch (e) {
      setError("Error connecting to AI Recruiter Assistant: " + e.message);
    } finally {
      setLoading(false);
    }
  };

  const selectedJob = recruiterJobs ? recruiterJobs.find(j => j.id === Number(selectedJobId)) : null;

  return (
    <div className="job-card" style={{ padding: '24px', borderRadius: '12px', background: 'rgba(17, 24, 39, 0.7)', border: '1px solid var(--glass-border)' }}>
      <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px', borderBottom: '1px solid var(--glass-border)', paddingBottom: '12px' }}>
        <div>
          <h2 style={{ fontSize: '22px', fontWeight: 'bold', color: 'var(--text-primary)', margin: 0 }}>
            🤖 AI Recruiter Assistant
          </h2>
          <p style={{ fontSize: '13px', color: 'var(--text-secondary)', margin: '4px 0 0 0' }}>
            Ask intelligent questions about applicants for your posted jobs
          </p>
        </div>
      </div>

      <div style={{ display: 'flex', flexDirection: 'column', gap: '20px' }}>
        {/* Job Selection Controls */}
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(280px, 1fr))', gap: '15px' }}>
          <div>
            <label style={{ display: 'block', fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '6px' }}>Select Job:</label>
            <select
              value={selectedJobId}
              onChange={(e) => {
                setSelectedJobId(e.target.value);
                setResponse(null);
                setError('');
              }}
              className="search-select"
              style={{ width: '100%', padding: '10px' }}
            >
              {recruiterJobs && recruiterJobs.length > 0 ? (
                recruiterJobs.map((j) => (
                  <option key={j.id} value={j.id}>
                    {j.title} ({j.location})
                  </option>
                ))
              ) : (
                <option value="">No posted jobs available</option>
              )}
            </select>
          </div>

          {selectedJob && (
            <div style={{ background: 'rgba(255,255,255,0.03)', padding: '10px 16px', borderRadius: '8px', border: '1px solid var(--glass-border)', display: 'flex', flexDirection: 'column', justifyContent: 'center' }}>
              <span style={{ fontSize: '12px', color: '#9ca3af' }}>Active Opening</span>
              <strong style={{ color: '#a78bfa', fontSize: '14px' }}>{selectedJob.title}</strong>
            </div>
          )}
        </div>

        {/* Question Input Section */}
        <div>
          <label style={{ display: 'block', fontSize: '13px', color: 'var(--text-secondary)', marginBottom: '6px' }}>Ask AI Question:</label>
          <div style={{ display: 'flex', gap: '10px' }}>
            <input
              type="text"
              placeholder="e.g. Find the best 5 candidates for this job"
              value={question}
              onChange={(e) => setQuestion(e.target.value)}
              onKeyDown={(e) => { if (e.key === 'Enter') handleAskAssistant(); }}
              className="search-input"
              style={{ flex: 1 }}
            />
            <button
              onClick={() => handleAskAssistant()}
              disabled={loading || !selectedJobId}
              className="search-btn"
              style={{ background: 'var(--primary)', padding: '10px 20px', fontWeight: 'bold' }}
            >
              {loading ? 'Analyzing...' : 'Ask AI'}
            </button>
          </div>

          {/* Quick Prompts */}
          <div style={{ display: 'flex', flexWrap: 'wrap', gap: '8px', marginTop: '12px' }}>
            <span style={{ fontSize: '12px', color: 'var(--text-secondary)', alignSelf: 'center' }}>Quick Prompts:</span>
            {sampleQuestions.map((q, idx) => (
              <button
                key={idx}
                onClick={() => {
                  setQuestion(q);
                  handleAskAssistant(q);
                }}
                disabled={loading}
                style={{
                  background: 'rgba(99, 102, 241, 0.15)',
                  border: '1px solid rgba(99, 102, 241, 0.3)',
                  color: '#a78bfa',
                  padding: '4px 10px',
                  borderRadius: '16px',
                  fontSize: '12px',
                  cursor: 'pointer'
                }}
              >
                {q}
              </button>
            ))}
          </div>
        </div>

        {/* Error Alert */}
        {error && (
          <div style={{ padding: '12px', borderRadius: '8px', backgroundColor: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#f87171', fontSize: '13px' }}>
            {error}
          </div>
        )}

        {/* AI Response Output Card */}
        {response && (
          <div style={{
            marginTop: '10px',
            padding: '20px',
            borderRadius: '12px',
            backgroundColor: 'rgba(30, 27, 75, 0.5)',
            border: '1px solid #6366f1',
            display: 'flex',
            flexDirection: 'column',
            gap: '15px'
          }}>
            <div>
              <span style={{ fontSize: '12px', color: '#818cf8', fontWeight: 'bold', textTransform: 'uppercase' }}>AI Recommendation Summary</span>
              <p style={{ margin: '8px 0 0 0', color: '#f3f4f6', fontSize: '14px', lineHeight: '1.6' }}>
                {response.answer}
              </p>
            </div>

            {/* Candidate List Breakdown */}
            {response.candidates && response.candidates.length > 0 && (
              <div>
                <h4 style={{ fontSize: '14px', color: '#a78bfa', marginBottom: '10px', borderBottom: '1px solid rgba(255,255,255,0.05)', paddingBottom: '6px' }}>
                  Ranked Candidates ({response.candidates.length})
                </h4>
                <div style={{ display: 'flex', flexDirection: 'column', gap: '10px' }}>
                  {response.candidates.map((c) => (
                    <div key={c.candidateId} style={{
                      background: 'rgba(255, 255, 255, 0.03)',
                      padding: '14px',
                      borderRadius: '8px',
                      border: '1px solid var(--glass-border)',
                      display: 'flex',
                      justify: 'space-between',
                      alignItems: 'center',
                      flexWrap: 'wrap',
                      gap: '10px'
                    }}>
                      <div>
                        <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                          <strong style={{ fontSize: '15px', color: '#f9fafb' }}>{c.candidateName}</strong>
                          <span style={{
                            fontSize: '14px',
                            fontWeight: 'bold',
                            color: c.matchScore >= 80 ? '#4ade80' : '#facc15'
                          }}>
                            {c.matchScore}% Match
                          </span>
                          <span className="badge" style={{ fontSize: '11px', backgroundColor: 'rgba(255,255,255,0.1)' }}>
                            {c.applicationStatus}
                          </span>
                        </div>

                        {c.reason && (
                          <p style={{ fontSize: '12px', color: '#9ca3af', margin: '4px 0 6px 0' }}>
                            {c.reason}
                          </p>
                        )}

                        <div style={{ display: 'flex', gap: '6px', flexWrap: 'wrap' }}>
                          {c.matchedSkills && c.matchedSkills.map((s, idx) => (
                            <span key={idx} style={{ fontSize: '11px', color: '#4ade80' }}>✓ {s}</span>
                          ))}
                          {c.missingSkills && c.missingSkills.map((s, idx) => (
                            <span key={idx} style={{ fontSize: '11px', color: '#f87171' }}>• {s}</span>
                          ))}
                        </div>
                      </div>

                      <div>
                        <button
                          onClick={() => onViewCandidate && onViewCandidate(c.candidateId, selectedJobId)}
                          className="search-btn"
                          style={{ padding: '6px 14px', fontSize: '12px', background: 'var(--primary)' }}
                        >
                          View Candidate
                        </button>
                      </div>
                    </div>
                  ))}
                </div>
              </div>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
