import { useState, useEffect } from 'react';
import RecruiterAssistant from './components/recruiter/RecruiterAssistant';
import './App.css';

const API_BASE = import.meta.env.VITE_API_URL || 'http://localhost:8080/api';

function App() {
  const [user, setUser] = useState(null);
  const [token, setToken] = useState(localStorage.getItem('token') || '');
  const [activeTab, setActiveTab] = useState('jobs'); // 'jobs' | 'dashboard' | 'profile' | 'post-job' | 'company-profile'
  
  // Auth state
  const [showAuthModal, setShowAuthModal] = useState(false);
  const [isRegister, setIsRegister] = useState(false);
  const [authEmail, setAuthEmail] = useState('');
  const [authPassword, setAuthPassword] = useState('');
  const [authName, setAuthName] = useState('');
  const [authRole, setAuthRole] = useState('JOB_SEEKER');
  
  // Job search/list state
  const [jobs, setJobs] = useState([]);
  const [recruiterJobs, setRecruiterJobs] = useState([]);
  const [searchKeyword, setSearchKeyword] = useState('');
  const [searchLocation, setSearchLocation] = useState('');
  const [searchType, setSearchType] = useState('');
  
  // Stats and applications state
  const [stats, setStats] = useState(null);
  const [applications, setApplications] = useState([]);
  const [companies, setCompanies] = useState([]);
  
  // Job detail / Apply state
  const [selectedJob, setSelectedJob] = useState(null);
  const [coverLetter, setCoverLetter] = useState('');
  const [showApplyModal, setShowApplyModal] = useState(false);
  
  // Profile form state
  const [profileName, setProfileName] = useState('');
  const [profilePhone, setProfilePhone] = useState('');
  const [profileLocation, setProfileLocation] = useState('');
  const [profileTitle, setProfileTitle] = useState(''); // Headline
  const [profileBio, setProfileBio] = useState(''); // Summary
  const [profileSkills, setProfileSkills] = useState('');
  const [profileEducation, setProfileEducation] = useState('');
  const [profileCollege, setProfileCollege] = useState('');
  const [profileGradYear, setProfileGradYear] = useState('');
  const [profileExperience, setProfileExperience] = useState('');
  const [profileGithub, setProfileGithub] = useState('');
  const [profileLinkedin, setProfileLinkedin] = useState('');
  const [isEditingProfile, setIsEditingProfile] = useState(false);

  // Recruiter Company form state
  const [compName, setCompName] = useState('');
  const [compDesc, setCompDesc] = useState('');
  const [compWebsite, setCompWebsite] = useState('');
  const [compLoc, setCompLoc] = useState('');
  const [compIndustry, setCompIndustry] = useState('');
  
  // Resume state
  const [resume, setResume] = useState(null);
  const [resumeFile, setResumeFile] = useState(null);
  const [resumeUploadStatus, setResumeUploadStatus] = useState('');

  // Recruiter actions state
  const [newJobTitle, setNewJobTitle] = useState('');
  const [newJobDesc, setNewJobDesc] = useState('');
  const [newJobLoc, setNewJobLoc] = useState('');
  const [newJobType, setNewJobType] = useState('FULL_TIME');
  const [newJobExp, setNewJobExp] = useState('Entry Level');
  const [newJobSalaryMin, setNewJobSalaryMin] = useState('');
  const [newJobSalaryMax, setNewJobSalaryMax] = useState('');
  const [newJobSkills, setNewJobSkills] = useState('');
  const [newJobReqs, setNewJobReqs] = useState('');
  const [selectedCompanyId, setSelectedCompanyId] = useState('');
  
  // Recruiter applicant view state
  const [jobApplicants, setJobApplicants] = useState([]);
  const [selectedApplicantJob, setSelectedApplicantJob] = useState(null);
  const [selectedCandidate, setSelectedCandidate] = useState(null);

  // Admin state
  const [adminUsers, setAdminUsers] = useState([]);
  const [adminJobs, setAdminJobs] = useState([]);
  const [adminCompanies, setAdminCompanies] = useState([]);
  const [adminApplications, setAdminApplications] = useState([]);
  const [adminUserSearch, setAdminUserSearch] = useState('');
  const [adminRoleFilter, setAdminRoleFilter] = useState('');
  const [adminJobSearch, setAdminJobSearch] = useState('');
  const [adminJobStatusFilter, setAdminJobStatusFilter] = useState('');
  const [adminCompanySearch, setAdminCompanySearch] = useState('');
  const [adminAppSearch, setAdminAppSearch] = useState('');
  const [adminAppStatusFilter, setAdminAppStatusFilter] = useState('');
  const [adminUserPage, setAdminUserPage] = useState(1);
  const [adminJobPage, setAdminJobPage] = useState(1);
  const [adminCompanyPage, setAdminCompanyPage] = useState(1);
  const [adminAppPage, setAdminAppPage] = useState(1);

  // Notification state
  const [notifications, setNotifications] = useState([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [showNotificationPanel, setShowNotificationPanel] = useState(false);

  // Load profile and jobs on boot
  useEffect(() => {
    fetchJobs();
    if (token) {
      fetchProfile();
      fetchResume();
      fetchNotifications();
      fetchUnreadCount();
    }
  }, [token]);

  useEffect(() => {
    if (user) {
      fetchStats();
      fetchNotifications();
      fetchUnreadCount();
      if (user.role === 'JOB_SEEKER') {
        fetchResume();
        fetchMyApplications();
        fetchRecommendedJobs();
        syncProfileForm(user);
        if (activeTab.startsWith('admin-')) setActiveTab('jobs');
      } else if (user.role === 'RECRUITER') {
        fetchRecruiterCompanies();
        fetchRecruiterApplications();
        fetchRecruiterJobs();
        if (activeTab.startsWith('admin-')) setActiveTab('jobs');
      } else if (user.role === 'ADMIN') {
        setActiveTab('admin-dashboard');
        fetchAdminUsers();
        fetchAdminJobs();
        fetchAdminCompanies();
        fetchAdminApplications();
      }
    }
  }, [user]);

  const fetchProfile = async () => {
    try {
      const res = await fetch(`${API_BASE}/users/profile`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setUser(data);
      } else {
        handleLogout();
      }
    } catch (e) {
      console.error("Error fetching profile", e);
    }
  };

  const syncProfileForm = (profileData) => {
    setProfileName(profileData.name || '');
    setProfilePhone(profileData.phone || '');
    setProfileLocation(profileData.location || '');
    setProfileTitle(profileData.title || '');
    setProfileBio(profileData.bio || '');
    setProfileSkills(profileData.skills || '');
    setProfileEducation(profileData.education || '');
    setProfileCollege(profileData.college || '');
    setProfileGradYear(profileData.graduationYear || '');
    setProfileExperience(profileData.experience || '');
    setProfileGithub(profileData.githubUrl || '');
    setProfileLinkedin(profileData.linkedinUrl || '');
  };

  const fetchResume = async () => {
    try {
      const res = await fetch(`${API_BASE}/resumes/my`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setResume(data);
        fetchResumeAnalysis(data.id);
      } else {
        setResume(null);
      }
    } catch (e) {
      setResume(null);
    }
  };

  // AI Resume Analysis state
  const [resumeAnalysis, setResumeAnalysis] = useState(null);
  const [isAnalyzing, setIsAnalyzing] = useState(false);
  const [analysisError, setAnalysisError] = useState('');

  const fetchResumeAnalysis = async (resumeId) => {
    if (!resumeId || !token) return;
    try {
      const res = await fetch(`${API_BASE}/resumes/${resumeId}/analysis`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setResumeAnalysis(data);
      }
    } catch (e) {
      console.log("No existing analysis found");
    }
  };

  // AI Job Recommendations state
  const [recommendedJobs, setRecommendedJobs] = useState([]);
  const [isLoadingRecommendations, setIsLoadingRecommendations] = useState(false);

  const fetchRecommendedJobs = async () => {
    if (!token) return;
    setIsLoadingRecommendations(true);
    try {
      const res = await fetch(`${API_BASE}/recommendations/jobs?limit=5`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setRecommendedJobs(data);
      }
    } catch (e) {
      console.log("Failed to fetch job recommendations: " + e.message);
    } finally {
      setIsLoadingRecommendations(false);
    }
  };

  // AI Job Match state
  const [jobMatchResults, setJobMatchResults] = useState({}); // { [jobId]: matchResponse }
  const [matchingJobId, setMatchingJobId] = useState(null);
  const [matchError, setMatchError] = useState('');

  const fetchJobMatch = async (jobId) => {
    if (!jobId || !token) return;
    try {
      const res = await fetch(`${API_BASE}/jobs/${jobId}/match`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setJobMatchResults(prev => ({ ...prev, [jobId]: data }));
      }
    } catch (e) {
      console.log("No existing job match found for job " + jobId);
    }
  };

  const handleMatchJob = async (jobId) => {
    setMatchingJobId(jobId);
    setMatchError('');
    try {
      const res = await fetch(`${API_BASE}/jobs/${jobId}/match`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setJobMatchResults(prev => ({ ...prev, [jobId]: data }));
      } else {
        const err = await res.json();
        setMatchError(err.message || "Failed to analyze job match");
        alert(err.message || "Failed to analyze job match");
      }
    } catch (e) {
      setMatchError("Error analyzing job match: " + e.message);
      alert("Error analyzing job match: " + e.message);
    } finally {
      setMatchingJobId(null);
    }
  };

  const handleAnalyzeResume = async () => {
    if (!resume) {
      alert("Please upload a resume first");
      return;
    }
    setIsAnalyzing(true);
    setAnalysisError('');
    try {
      const res = await fetch(`${API_BASE}/resumes/${resume.id}/analyze`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setResumeAnalysis(data);
      } else {
        const err = await res.json();
        setAnalysisError(err.message || "Failed to analyze resume");
      }
    } catch (e) {
      setAnalysisError("Error requesting AI analysis: " + e.message);
    } finally {
      setIsAnalyzing(false);
    }
  };

  const handleResumeUpload = async (e) => {
    e.preventDefault();
    if (!resumeFile) {
      alert("Please select a file first");
      return;
    }
    if (resumeFile.type !== "application/pdf") {
      alert("Only PDF files are allowed");
      return;
    }
    
    setResumeUploadStatus('Uploading...');
    const formData = new FormData();
    formData.append('file', resumeFile);

    try {
      const res = await fetch(`${API_BASE}/resumes/upload`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` },
        body: formData
      });

      if (res.ok) {
        setResumeUploadStatus('Upload successful');
        fetchResume();
      } else {
        const err = await res.json();
        setResumeUploadStatus(err.message || 'Upload failed');
      }
    } catch (e) {
      setResumeUploadStatus('Upload failed');
    }
  };

  const handleResumeDelete = async () => {
    if (!resume) return;
    if (!confirm("Are you sure you want to delete your resume?")) return;

    try {
      const res = await fetch(`${API_BASE}/resumes/${resume.id}`, {
        method: 'DELETE',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        setResume(null);
        setResumeFile(null);
        setResumeUploadStatus('');
        alert("Resume deleted successfully");
      } else {
        alert("Failed to delete resume");
      }
    } catch (e) {
      alert("Error deleting resume: " + e.message);
    }
  };

  const fetchJobs = async () => {
    try {
      const res = await fetch(`${API_BASE}/jobs`);
      if (res.ok) {
        const data = await res.json();
        setJobs(data);
      }
    } catch (e) {
      console.error("Error fetching jobs", e);
    }
  };

  const fetchRecruiterJobs = async () => {
    try {
      const res = await fetch(`${API_BASE}/jobs/recruiter`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setRecruiterJobs(data);
      }
    } catch (e) {
      console.error("Error fetching recruiter jobs", e);
    }
  };

  const handleSearch = async (e) => {
    e.preventDefault();
    try {
      let url = `${API_BASE}/jobs/search?`;
      if (searchKeyword) url += `keyword=${encodeURIComponent(searchKeyword)}&`;
      if (searchLocation) url += `location=${encodeURIComponent(searchLocation)}&`;
      if (searchType) url += `jobType=${searchType}&`;
      
      const res = await fetch(url);
      if (res.ok) {
        const data = await res.json();
        setJobs(data);
      }
    } catch (e) {
      console.error("Error searching jobs", e);
    }
  };

  const fetchStats = async () => {
    try {
      const res = await fetch(`${API_BASE}/users/stats`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setStats(data);
      }
    } catch (e) {
      console.error("Error fetching stats", e);
    }
  };

  const fetchMyApplications = async () => {
    try {
      const res = await fetch(`${API_BASE}/applications/my`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setApplications(data);
      }
    } catch (e) {
      console.error("Error fetching applications", e);
    }
  };

  const fetchRecruiterCompanies = async () => {
    try {
      const res = await fetch(`${API_BASE}/companies/recruiter`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setCompanies(data);
        if (data.length > 0) {
          setSelectedCompanyId(data[0].id);
          setCompName(data[0].name || '');
          setCompDesc(data[0].description || '');
          setCompWebsite(data[0].website || '');
          setCompLoc(data[0].location || '');
          setCompIndustry(data[0].industry || '');
        }
      }
    } catch (e) {
      console.error("Error fetching companies", e);
    }
  };

  const fetchRecruiterApplications = async () => {
    try {
      const res = await fetch(`${API_BASE}/applications/recruiter`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setApplications(data);
      }
    } catch (e) {
      console.error("Error fetching recruiter applications", e);
    }
  };

  const fetchAdminUsers = async () => {
    try {
      const res = await fetch(`${API_BASE}/admin/users`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setAdminUsers(data);
      }
    } catch (e) {
      console.error("Error fetching admin users", e);
    }
  };

  const fetchAdminJobs = async () => {
    try {
      const res = await fetch(`${API_BASE}/admin/jobs`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setAdminJobs(data);
      }
    } catch (e) {
      console.error("Error fetching admin jobs", e);
    }
  };

  const fetchAdminCompanies = async () => {
    try {
      const res = await fetch(`${API_BASE}/admin/companies`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setAdminCompanies(data);
      }
    } catch (e) {
      console.error("Error fetching admin companies", e);
    }
  };

  const fetchAdminApplications = async () => {
    try {
      const res = await fetch(`${API_BASE}/admin/applications`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setAdminApplications(data);
      }
    } catch (e) {
      console.error("Error fetching admin applications", e);
    }
  };

  const toggleUserBlockStatus = async (userId, currentStatus) => {
    const newStatus = currentStatus === 'BLOCKED' ? 'ACTIVE' : 'BLOCKED';
    try {
      const res = await fetch(`${API_BASE}/admin/users/${userId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ status: newStatus })
      });
      if (res.ok) {
        alert(`User ${newStatus === 'BLOCKED' ? 'blocked' : 'unblocked'} successfully!`);
        fetchAdminUsers();
        fetchStats();
      } else {
        alert("Failed to update user status");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const updateAdminJobStatus = async (jobId, newStatus) => {
    try {
      const res = await fetch(`${API_BASE}/admin/jobs/${jobId}/status`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ status: newStatus })
      });
      if (res.ok) {
        alert(`Job status changed to ${newStatus}`);
        fetchAdminJobs();
        fetchStats();
      } else {
        alert("Failed to update job status");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const fetchNotifications = async () => {
    if (!token) return;
    try {
      const res = await fetch(`${API_BASE}/notifications`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setNotifications(data);
      }
    } catch (e) {
      console.error("Error fetching notifications", e);
    }
  };

  const fetchUnreadCount = async () => {
    if (!token) return;
    try {
      const res = await fetch(`${API_BASE}/notifications/unread-count`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const count = await res.json();
        setUnreadCount(count);
      }
    } catch (e) {
      console.error("Error fetching unread count", e);
    }
  };

  const markNotificationAsRead = async (id) => {
    try {
      const res = await fetch(`${API_BASE}/notifications/${id}/read`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        fetchNotifications();
        fetchUnreadCount();
      }
    } catch (e) {
      console.error("Error marking notification as read", e);
    }
  };

  const markAllNotificationsAsRead = async () => {
    try {
      const res = await fetch(`${API_BASE}/notifications/read-all`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        fetchNotifications();
        fetchUnreadCount();
      }
    } catch (e) {
      console.error("Error marking all as read", e);
    }
  };

  const handleAuth = async (e) => {
    e.preventDefault();
    const endpoint = isRegister ? '/auth/register' : '/auth/login';
    const body = isRegister 
      ? { name: authName, email: authEmail, password: authPassword, role: authRole }
      : { email: authEmail, password: authPassword };

    try {
      const res = await fetch(`${API_BASE}${endpoint}`, {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(body)
      });

      if (res.ok) {
        if (isRegister) {
          setIsRegister(false);
          alert("Registration successful. Please login.");
        } else {
          const data = await res.json();
          localStorage.setItem('token', data.token);
          setToken(data.token);
          setShowAuthModal(false);
        }
      } else {
        const errText = await res.text();
        alert(errText || "Authentication failed");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const handleSaveProfile = async (e) => {
    e.preventDefault();
    const updateData = {
      name: profileName,
      phone: profilePhone,
      location: profileLocation,
      title: profileTitle,
      bio: profileBio,
      skills: profileSkills,
      education: profileEducation,
      college: profileCollege,
      graduationYear: profileGradYear ? parseInt(profileGradYear) : null,
      experience: profileExperience,
      githubUrl: profileGithub,
      linkedinUrl: profileLinkedin
    };

    try {
      const res = await fetch(`${API_BASE}/candidates/profile`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(updateData)
      });

      if (res.ok) {
        const data = await res.json();
        setUser(data);
        setIsEditingProfile(false);
        alert("Profile updated successfully!");
      } else {
        alert("Failed to update profile");
      }
    } catch (e) {
      alert("Error updating profile: " + e.message);
    }
  };

  const handleSaveCompany = async (e) => {
    e.preventDefault();
    if (companies.length === 0) return;
    const companyId = companies[0].id;
    try {
      const res = await fetch(`${API_BASE}/companies/${companyId}`, {
        method: 'PUT',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({
          name: compName,
          description: compDesc,
          website: compWebsite,
          location: compLoc,
          industry: compIndustry
        })
      });
      if (res.ok) {
        alert("Company profile updated successfully!");
        fetchRecruiterCompanies();
      } else {
        alert("Failed to update company profile");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const handleLogout = () => {
    localStorage.removeItem('token');
    setToken('');
    setUser(null);
    setStats(null);
    setApplications([]);
    setCompanies([]);
    setResume(null);
    setActiveTab('jobs');
  };

  const handleApply = async (e) => {
    e.preventDefault();
    try {
      const res = await fetch(`${API_BASE}/applications/job/${selectedJob.id}?coverLetter=${encodeURIComponent(coverLetter)}`, {
        method: 'POST',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        alert("Application submitted successfully!");
        setShowApplyModal(false);
        fetchMyApplications();
      } else {
        const err = await res.json();
        alert(err.message || "Failed to submit application");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const handleCreateJob = async (e) => {
    e.preventDefault();
    if (companies.length === 0) {
      alert("Please add a company first before posting a job.");
      return;
    }
    const jobData = {
      title: newJobTitle,
      description: newJobDesc,
      location: newJobLoc,
      jobType: newJobType,
      experienceLevel: newJobExp,
      salaryMin: parseFloat(newJobSalaryMin),
      salaryMax: parseFloat(newJobSalaryMax),
      skills: newJobSkills,
      requirements: newJobReqs,
      companyId: parseInt(selectedCompanyId)
    };

    try {
      const res = await fetch(`${API_BASE}/jobs`, {
        method: 'POST',
        headers: { 
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify(jobData)
      });

      if (res.ok) {
        alert("Job posted successfully!");
        setActiveTab('jobs');
        fetchRecruiterJobs();
        fetchJobs();
      } else {
        alert("Failed to post job");
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const handleCreateCompany = async (e) => {
    e.preventDefault();
    const compName = prompt("Enter Company Name:");
    if (!compName) return;
    const compWebsite = prompt("Enter Company Website:");
    const compIndustry = prompt("Enter Industry:");

    try {
      const res = await fetch(`${API_BASE}/companies`, {
        method: 'POST',
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${token}`
        },
        body: JSON.stringify({ name: compName, website: compWebsite, industry: compIndustry })
      });
      if (res.ok) {
        alert("Company created!");
        fetchRecruiterCompanies();
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const updateApplicationStatus = async (appId, newStatus) => {
    try {
      const res = await fetch(`${API_BASE}/applications/${appId}/status?status=${newStatus}`, {
        method: 'PUT',
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        alert("Status updated!");
        fetchRecruiterApplications();
        if (selectedApplicantJob) {
          fetchApplicantsForJob(selectedApplicantJob.id);
        }
      }
    } catch (e) {
      alert("Error: " + e.message);
    }
  };

  const fetchApplicantsForJob = async (jobId) => {
    try {
      const res = await fetch(`${API_BASE}/applications/job/${jobId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setJobApplicants(data);
      }
    } catch (e) {
      console.error("Error fetching job applicants", e);
    }
  };

  const fetchCandidateProfile = async (candidateId) => {
    try {
      const res = await fetch(`${API_BASE}/recruiters/candidates/${candidateId}`, {
        headers: { 'Authorization': `Bearer ${token}` }
      });
      if (res.ok) {
        const data = await res.json();
        setSelectedCandidate(data);
      }
    } catch (e) {
      console.error("Error fetching candidate profile", e);
    }
  };

  const calculateProfileCompletion = () => {
    if (!user) return 0;
    const fields = [
      user.name, user.phone, user.location, user.title, user.bio,
      user.skills, user.education, user.college, user.graduationYear,
      user.experience, user.githubUrl, user.linkedinUrl, resume
    ];
    const completed = fields.filter(f => f !== null && f !== undefined && f !== '').length;
    return Math.round((completed / fields.length) * 100);
  };

  const getStatusClass = (status) => {
    return `status-badge status-${status.toLowerCase()}`;
  };

  return (
    <div className="App">
      {/* Navbar */}
      <nav className="navbar">
        <div className="logo-container">
          <span className="logo-text">HireSphere.ai</span>
        </div>
        <div className="nav-links">
          {(!user || user.role === 'JOB_SEEKER') && (
            <button className={`nav-btn ${activeTab === 'jobs' ? 'active' : ''}`} onClick={() => setActiveTab('jobs')}>
              Explore Jobs
            </button>
          )}
          {user && user.role !== 'ADMIN' && (
            <button className={`nav-btn ${activeTab === 'dashboard' ? 'active' : ''}`} onClick={() => setActiveTab('dashboard')}>
              Dashboard
            </button>
          )}
          {user && user.role === 'ADMIN' && (
            <>
              <button className={`nav-btn ${activeTab === 'admin-dashboard' ? 'active' : ''}`} onClick={() => setActiveTab('admin-dashboard')}>
                Dashboard
              </button>
              <button className={`nav-btn ${activeTab === 'admin-users' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-users'); fetchAdminUsers(); }}>
                Users
              </button>
              <button className={`nav-btn ${activeTab === 'admin-recruiters' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-recruiters'); fetchAdminUsers(); }}>
                Recruiters
              </button>
              <button className={`nav-btn ${activeTab === 'admin-candidates' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-candidates'); fetchAdminUsers(); }}>
                Candidates
              </button>
              <button className={`nav-btn ${activeTab === 'admin-companies' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-companies'); fetchAdminCompanies(); }}>
                Companies
              </button>
              <button className={`nav-btn ${activeTab === 'admin-jobs' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-jobs'); fetchAdminJobs(); }}>
                Jobs
              </button>
              <button className={`nav-btn ${activeTab === 'admin-applications' ? 'active' : ''}`} onClick={() => { setActiveTab('admin-applications'); fetchAdminApplications(); }}>
                Applications
              </button>
            </>
          )}
          {user && user.role === 'JOB_SEEKER' && (
            <button className={`nav-btn ${activeTab === 'profile' ? 'active' : ''}`} onClick={() => setActiveTab('profile')}>
              My Profile
            </button>
          )}
          {user && user.role === 'RECRUITER' && (
            <>
              <button className={`nav-btn ${activeTab === 'jobs' ? 'active' : ''}`} onClick={() => { setActiveTab('jobs'); fetchRecruiterJobs(); }}>
                My Jobs
              </button>
              <button className={`nav-btn ${activeTab === 'recruiter-assistant' ? 'active' : ''}`} onClick={() => { setActiveTab('recruiter-assistant'); fetchRecruiterJobs(); }}>
                🤖 AI Assistant
              </button>
              <button className={`nav-btn ${activeTab === 'post-job' ? 'active' : ''}`} onClick={() => setActiveTab('post-job')}>
                Post Job
              </button>
              <button className={`nav-btn ${activeTab === 'company-profile' ? 'active' : ''}`} onClick={() => setActiveTab('company-profile')}>
                Company Profile
              </button>
            </>
          )}
          {user ? (
            <div style={{ display: 'flex', gap: '15px', alignItems: 'center', position: 'relative' }}>
              {/* Notification Bell */}
              <div style={{ position: 'relative', cursor: 'pointer' }} onClick={() => setShowNotificationPanel(!showNotificationPanel)}>
                <span style={{ fontSize: '20px' }}>🔔</span>
                {unreadCount > 0 && (
                  <span style={{
                    position: 'absolute',
                    top: '-6px',
                    right: '-8px',
                    background: '#ef4444',
                    color: 'white',
                    borderRadius: '50%',
                    padding: '2px 6px',
                    fontSize: '11px',
                    fontWeight: 'bold'
                  }}>
                    {unreadCount}
                  </span>
                )}
              </div>

              {/* Notification Dropdown Panel */}
              {showNotificationPanel && (
                <div style={{
                  position: 'absolute',
                  top: '40px',
                  right: '0',
                  width: '320px',
                  backgroundColor: '#1e1b4b',
                  border: '1px solid #4338ca',
                  borderRadius: '8px',
                  boxShadow: '0 10px 25px rgba(0,0,0,0.5)',
                  zIndex: 1000,
                  maxHeight: '400px',
                  overflowY: 'auto',
                  padding: '12px'
                }}>
                  <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '10px', borderBottom: '1px solid #312e81', pb: '8px' }}>
                    <h4 style={{ margin: 0, color: '#f3f4f6' }}>Notifications</h4>
                    <button style={{ background: 'none', border: 'none', color: '#818cf8', cursor: 'pointer', fontSize: '12px' }} onClick={markAllNotificationsAsRead}>
                      Mark all as read
                    </button>
                  </div>
                  {notifications.length === 0 ? (
                    <p style={{ color: '#9ca3af', fontSize: '13px', textAlign: 'center', margin: '20px 0' }}>No notifications</p>
                  ) : (
                    notifications.map(n => (
                      <div 
                        key={n.id} 
                        onClick={() => markNotificationAsRead(n.id)}
                        style={{
                          padding: '10px',
                          borderRadius: '6px',
                          marginBottom: '8px',
                          backgroundColor: n.isRead ? 'rgba(255, 255, 255, 0.03)' : 'rgba(99, 102, 241, 0.15)',
                          borderLeft: n.isRead ? '3px solid transparent' : '3px solid #6366f1',
                          cursor: 'pointer'
                        }}
                      >
                        <div style={{ display: 'flex', alignItems: 'center', gap: '6px' }}>
                          {!n.isRead && <span style={{ color: '#6366f1', fontSize: '12px' }}>●</span>}
                          <strong style={{ color: n.isRead ? '#d1d5db' : '#ffffff', fontSize: '13px' }}>{n.title}</strong>
                        </div>
                        <p style={{ margin: '4px 0 0 0', color: '#9ca3af', fontSize: '12px' }}>{n.message}</p>
                        <span style={{ fontSize: '10px', color: '#6b7280', display: 'block', marginTop: '4px' }}>
                          {new Date(n.createdAt).toLocaleString()}
                        </span>
                      </div>
                    ))
                  )}
                </div>
              )}

              <span style={{ fontSize: '14px', color: '#c084fc' }}>({user.role}) {user.name}</span>
              <button className="nav-btn nav-btn-primary" onClick={handleLogout}>Logout</button>
            </div>
          ) : (
            <button className="nav-btn nav-btn-primary" onClick={() => { setShowAuthModal(true); setIsRegister(false); }}>
              Sign In
            </button>
          )}
        </div>
      </nav>

      {/* Hero Section */}
      {activeTab === 'jobs' && (!user || user.role !== 'RECRUITER') && (
        <header className="hero-section">
          <h1 className="hero-title">Hire smarter, land faster.</h1>
          <p className="hero-subtitle">
            Connecting top talent with leading enterprises using agentic AI matches.
          </p>
          <form className="search-container" onSubmit={handleSearch}>
            <input 
              type="text" 
              placeholder="Job title, keywords, or skills..." 
              className="search-input"
              value={searchKeyword}
              onChange={(e) => setSearchKeyword(e.target.value)}
            />
            <input 
              type="text" 
              placeholder="Location..." 
              className="search-input"
              value={searchLocation}
              onChange={(e) => setSearchLocation(e.target.value)}
            />
            <select 
              className="search-select"
              value={searchType}
              onChange={(e) => setSearchType(e.target.value)}
            >
              <option value="">Any Job Type</option>
              <option value="FULL_TIME">Full Time</option>
              <option value="PART_TIME">Part Time</option>
              <option value="CONTRACT">Contract</option>
              <option value="REMOTE">Remote</option>
              <option value="HYBRID">Hybrid</option>
            </select>
            <button type="submit" className="search-btn">Search</button>
          </form>
        </header>
      )}

      {/* Content Grid */}
      <div className="dashboard-grid">
        {/* Main Area */}
        <main>
          {activeTab === 'jobs' && user && user.role === 'JOB_SEEKER' && recommendedJobs.length > 0 && (
            <div className="recommended-section" style={{ marginBottom: '30px' }}>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px', marginBottom: '15px' }}>
                <h2 style={{ fontSize: '20px', fontWeight: '600', color: 'var(--text-primary)', margin: 0 }}>
                  🤖 AI Recommended Jobs
                </h2>
                <span className="badge badge-primary" style={{ fontSize: '12px' }}>Based on your profile & resume</span>
              </div>

              <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(320px, 1fr))', gap: '15px' }}>
                {recommendedJobs.map((rec) => {
                  const targetJob = jobs.find(j => j.id === rec.jobId) || {
                    id: rec.jobId,
                    title: rec.jobTitle,
                    companyName: rec.companyName,
                    location: rec.location,
                    jobType: rec.jobType,
                    description: rec.reason
                  };
                  const hasApplied = rec.alreadyApplied || applications.some(a => a.jobId === rec.jobId);

                  return (
                    <div className="job-card" key={rec.jobId} style={{
                      border: '1px solid #6366f1',
                      backgroundColor: 'rgba(30, 27, 75, 0.4)',
                      padding: '16px',
                      borderRadius: '12px',
                      display: 'flex',
                      flexDirection: 'column',
                      justify: 'space-between'
                    }}>
                      <div>
                        <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'flex-start' }}>
                          <div>
                            <h3 className="job-title" style={{ fontSize: '16px', marginBottom: '2px' }}>{rec.jobTitle}</h3>
                            <span className="job-company" style={{ fontSize: '13px' }}>{rec.companyName} • {rec.location}</span>
                          </div>
                          <span style={{
                            fontSize: '18px',
                            fontWeight: 'bold',
                            color: rec.matchScore >= 80 ? '#4ade80' : '#facc15'
                          }}>
                            {rec.matchScore}%
                          </span>
                        </div>

                        <p style={{ fontSize: '12px', color: '#a78bfa', marginTop: '8px', marginBottom: '10px', fontStyle: 'italic' }}>
                          "{rec.reason}"
                        </p>

                        <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginBottom: '12px' }}>
                          {rec.matchedSkills && rec.matchedSkills.map((sk, idx) => (
                            <span key={idx} style={{ fontSize: '11px', background: 'rgba(74, 222, 128, 0.15)', color: '#4ade80', padding: '2px 6px', borderRadius: '4px' }}>
                              ✓ {sk}
                            </span>
                          ))}
                          {rec.missingSkills && rec.missingSkills.map((sk, idx) => (
                            <span key={idx} style={{ fontSize: '11px', background: 'rgba(248, 113, 113, 0.15)', color: '#f87171', padding: '2px 6px', borderRadius: '4px' }}>
                              • {sk}
                            </span>
                          ))}
                        </div>
                      </div>

                      <div style={{ display: 'flex', gap: '8px', marginTop: '10px' }}>
                        <button 
                          className="search-btn"
                          style={{ flex: 1, padding: '6px 12px', fontSize: '12px' }}
                          onClick={() => { setSelectedJob(targetJob); setShowApplyModal(true); }}
                        >
                          View Job
                        </button>
                        <button 
                          className="search-btn"
                          disabled={hasApplied}
                          onClick={() => { setSelectedJob(targetJob); setShowApplyModal(true); }}
                          style={hasApplied ? { flex: 1, padding: '6px 12px', fontSize: '12px', background: '#2e303a', color: 'var(--text-secondary)', cursor: 'not-allowed' } : { flex: 1, padding: '6px 12px', fontSize: '12px', background: 'var(--primary)' }}
                        >
                          {hasApplied ? (rec.applicationStatus || 'Applied') : 'Apply'}
                        </button>
                      </div>
                    </div>
                  );
                })}
              </div>
            </div>
          )}

          {activeTab === 'jobs' && (!user || user.role !== 'RECRUITER') && (
            <div className="jobs-list">
              <h2>Active Openings ({jobs.length})</h2>
              {jobs.length === 0 ? (
                <p style={{ color: 'var(--text-secondary)' }}>No jobs found matching search criteria.</p>
              ) : (
                jobs.map((job) => {
                  const hasApplied = applications.some(app => app.jobId === job.id);
                  return (
                    <div className="job-card" key={job.id}>
                      <div className="job-header">
                        <div>
                          <h3 className="job-title">{job.title}</h3>
                          <span className="job-company">{job.companyName}</span>
                        </div>
                        {user && user.role === 'JOB_SEEKER' && (
                          <div style={{ display: 'flex', gap: '10px' }}>
                            <button 
                              className="search-btn"
                              style={{ background: 'var(--primary)' }}
                              onClick={() => handleMatchJob(job.id)}
                              disabled={matchingJobId === job.id}
                            >
                              {matchingJobId === job.id ? 'Analyzing...' : 'Analyze Job Match'}
                            </button>
                            <button 
                              className="search-btn"
                              disabled={hasApplied}
                              onClick={() => { setSelectedJob(job); setShowApplyModal(true); }}
                              style={hasApplied ? { background: '#2e303a', color: 'var(--text-secondary)', cursor: 'not-allowed' } : {}}
                            >
                              {hasApplied ? 'Applied' : 'Apply Now'}
                            </button>
                          </div>
                        )}
                      </div>
                      <div className="job-badges">
                        <span className="badge badge-primary">{job.jobType}</span>
                        <span className="badge">{job.location}</span>
                        <span className="badge">{job.experienceLevel}</span>
                      </div>
                      <p className="job-desc">{job.description}</p>
                      
                      {/* Job Match Result Card */}
                      {jobMatchResults[job.id] && (
                        <div style={{
                          marginTop: '15px',
                          padding: '15px',
                          borderRadius: '8px',
                          backgroundColor: 'rgba(30, 27, 75, 0.6)',
                          border: '1px solid #6366f1',
                          display: 'flex',
                          flexDirection: 'column',
                          gap: '10px'
                        }}>
                          <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', borderBottom: '1px solid #312e81', pb: '8px' }}>
                            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                              <span style={{ fontSize: '24px', fontWeight: 'bold', color: jobMatchResults[job.id].matchScore >= 80 ? '#4ade80' : jobMatchResults[job.id].matchScore >= 60 ? '#facc15' : '#f87171' }}>
                                {jobMatchResults[job.id].matchScore}%
                              </span>
                              <span className="badge" style={{ backgroundColor: 'rgba(99, 102, 241, 0.3)', color: '#a78bfa', fontWeight: 'bold' }}>
                                {jobMatchResults[job.id].recommendation}
                              </span>
                            </div>
                            <span style={{ fontSize: '12px', color: '#9ca3af' }}>AI Match Report</span>
                          </div>

                          <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '10px', fontSize: '13px' }}>
                            <div>
                              <strong style={{ color: '#4ade80' }}>Matched Skills:</strong>
                              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '4px' }}>
                                {jobMatchResults[job.id].matchedSkills && jobMatchResults[job.id].matchedSkills.length > 0 ? (
                                  jobMatchResults[job.id].matchedSkills.map((s, idx) => (
                                    <span key={idx} style={{ color: '#4ade80', fontSize: '12px' }}>✓ {s}</span>
                                  ))
                                ) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>None</span>}
                              </div>
                            </div>
                            <div>
                              <strong style={{ color: '#f87171' }}>Missing Skills:</strong>
                              <div style={{ display: 'flex', flexWrap: 'wrap', gap: '4px', marginTop: '4px' }}>
                                {jobMatchResults[job.id].missingSkills && jobMatchResults[job.id].missingSkills.length > 0 ? (
                                  jobMatchResults[job.id].missingSkills.map((s, idx) => (
                                    <span key={idx} style={{ color: '#f87171', fontSize: '12px' }}>• {s}</span>
                                  ))
                                ) : <span style={{ color: '#4ade80', fontSize: '12px' }}>None missing!</span>}
                              </div>
                            </div>
                          </div>

                          <div style={{ fontSize: '12px', color: '#d1d5db', borderTop: '1px solid rgba(255,255,255,0.05)', paddingTop: '6px' }}>
                            <strong>Experience Alignment:</strong> {jobMatchResults[job.id].matchingExperience}
                          </div>

                          <div style={{ fontSize: '12px', color: '#d1d5db' }}>
                            <strong>Education Alignment:</strong> {jobMatchResults[job.id].educationMatch}
                          </div>
                        </div>
                      )}

                      <div className="job-footer">
                        <span className="job-salary">
                          {job.salaryMin && job.salaryMax ? `$${job.salaryMin} - $${job.salaryMax}` : 'Salary Undisclosed'}
                        </span>
                        <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>
                          Posted: {new Date(job.createdAt).toLocaleDateString()}
                        </span>
                      </div>
                    </div>
                  );
                })
              )}
            </div>
          )}

          {activeTab === 'jobs' && user && user.role === 'RECRUITER' && (
            <div className="jobs-list">
              <h2>My Posted Jobs ({recruiterJobs.length})</h2>
              {recruiterJobs.length === 0 ? (
                <p style={{ color: 'var(--text-secondary)' }}>You haven't posted any jobs yet.</p>
              ) : (
                recruiterJobs.map((job) => (
                  <div className="job-card" key={job.id}>
                    <div className="job-header">
                      <div>
                        <h3 className="job-title">{job.title}</h3>
                        <span className="job-company">{job.companyName}</span>
                      </div>
                      <button 
                        className="search-btn"
                        style={{ background: 'var(--info)' }}
                        onClick={() => { setSelectedApplicantJob(job); fetchApplicantsForJob(job.id); }}
                      >
                        View Candidates
                      </button>
                    </div>
                    <div className="job-badges">
                      <span className="badge badge-primary">{job.jobType}</span>
                      <span className="badge">{job.location}</span>
                      <span className="badge">{job.experienceLevel}</span>
                    </div>
                    <p className="job-desc">{job.description}</p>
                  </div>
                ))
              )}
            </div>
          )}

          {activeTab === 'dashboard' && user && user.role === 'JOB_SEEKER' && (
            <div>
              <h2>Candidate Dashboard</h2>
              {stats && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(140px, 1fr))', gap: '15px', marginBottom: '25px' }}>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalApplications || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Applications</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.shortlisted || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Shortlisted</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.interviews || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Interviews</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '24px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.hired || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Hired</div>
                  </div>
                </div>
              )}

              {/* Recent Notifications Widget */}
              <div className="job-card" style={{ marginBottom: '25px' }}>
                <h3>Recent Notifications</h3>
                {notifications.length === 0 ? (
                  <p style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>No notifications yet.</p>
                ) : (
                  <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '10px' }}>
                    {notifications.slice(0, 3).map(n => (
                      <div key={n.id} style={{ padding: '10px', borderRadius: '6px', background: 'rgba(255,255,255,0.03)', borderLeft: '3px solid #6366f1' }}>
                        <div style={{ fontWeight: 'bold', fontSize: '14px' }}>{n.title}</div>
                        <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>{n.message}</div>
                        <div style={{ fontSize: '11px', color: '#6b7280', marginTop: '4px' }}>{new Date(n.createdAt).toLocaleString()}</div>
                      </div>
                    ))}
                  </div>
                )}
              </div>

              <h2>Your Applications ({applications.length})</h2>
              <div className="jobs-list">
                {applications.length === 0 ? (
                  <p style={{ color: 'var(--text-secondary)' }}>You haven't applied for any jobs yet.</p>
                ) : (
                  applications.map((app) => (
                    <div className="job-card" key={app.id}>
                      <div className="job-header">
                        <div>
                          <h3 className="job-title">{app.jobTitle}</h3>
                          <span className="job-company">{app.companyName}</span>
                        </div>
                        <span className={getStatusClass(app.status)}>{app.status}</span>
                      </div>
                      <div style={{ marginTop: '10px', fontSize: '14px', color: 'var(--text-secondary)' }}>
                        <strong>Cover Letter:</strong> {app.coverLetter || 'No cover letter provided.'}
                      </div>
                    </div>
                  ))
                )}
              </div>
            </div>
          )}

          {activeTab === 'profile' && user && user.role === 'JOB_SEEKER' && (
            <div className="job-card">
              <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginBottom: '20px' }}>
                <h2>My Candidate Profile</h2>
                {!isEditingProfile && (
                  <button className="search-btn" onClick={() => setIsEditingProfile(true)}>Edit Profile</button>
                )}
              </div>

              {isEditingProfile ? (
                <form onSubmit={handleSaveProfile}>
                  <div className="form-group">
                    <label className="form-label">Full Name</label>
                    <input type="text" className="form-input" required value={profileName} onChange={(e) => setProfileName(e.target.value)} />
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                    <div className="form-group">
                      <label className="form-label">Phone</label>
                      <input type="text" className="form-input" value={profilePhone} onChange={(e) => setProfilePhone(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Location</label>
                      <input type="text" className="form-input" value={profileLocation} onChange={(e) => setProfileLocation(e.target.value)} />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Headline / Title</label>
                    <input type="text" className="form-input" placeholder="e.g. Senior Java Developer | AWS Certified" value={profileTitle} onChange={(e) => setProfileTitle(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Professional Summary</label>
                    <textarea className="form-textarea" rows="4" value={profileBio} onChange={(e) => setProfileBio(e.target.value)}></textarea>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Skills (comma-separated)</label>
                    <input type="text" className="form-input" placeholder="e.g. Java, React, SQL" value={profileSkills} onChange={(e) => setProfileSkills(e.target.value)} />
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '2fr 1fr', gap: '20px' }}>
                    <div className="form-group">
                      <label className="form-label">College / University</label>
                      <input type="text" className="form-input" value={profileCollege} onChange={(e) => setProfileCollege(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label className="form-label">Graduation Year</label>
                      <input type="number" className="form-input" value={profileGradYear} onChange={(e) => setProfileGradYear(e.target.value)} />
                    </div>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Education details</label>
                    <textarea className="form-textarea" rows="3" placeholder="e.g. B.Tech in Computer Science" value={profileEducation} onChange={(e) => setProfileEducation(e.target.value)}></textarea>
                  </div>
                  <div className="form-group">
                    <label className="form-label">Work Experience</label>
                    <textarea className="form-textarea" rows="4" placeholder="Explain your past job history..." value={profileExperience} onChange={(e) => setProfileExperience(e.target.value)}></textarea>
                  </div>
                  <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                    <div className="form-group">
                      <label className="form-label">GitHub URL</label>
                      <input type="url" className="form-input" value={profileGithub} onChange={(e) => setProfileGithub(e.target.value)} />
                    </div>
                    <div className="form-group">
                      <label className="form-label">LinkedIn URL</label>
                      <input type="url" className="form-input" value={profileLinkedin} onChange={(e) => setProfileLinkedin(e.target.value)} />
                    </div>
                  </div>
                  <div style={{ display: 'flex', gap: '15px' }}>
                    <button type="submit" className="search-btn">Save Profile</button>
                    <button type="button" className="nav-btn" onClick={() => { setIsEditingProfile(false); syncProfileForm(user); }}>Cancel</button>
                  </div>
                </form>
              ) : (
                <div style={{ display: 'flex', flexDirection: 'column', gap: '20px', textAlign: 'left' }}>
                  <div>
                    <h3 style={{ margin: '0 0 5px 0', fontSize: '24px' }}>{user.name}</h3>
                    <p style={{ color: 'var(--primary)', fontWeight: 'bold' }}>{user.title || 'Add headline / professional title'}</p>
                    <p style={{ color: 'var(--text-secondary)' }}>{user.location || 'Location not set'} | {user.phone || 'Phone not set'}</p>
                  </div>
                  
                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Professional Summary:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px', whiteSpace: 'pre-line' }}>{user.bio || 'No summary added yet.'}</p>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Skills:</strong>
                    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '8px' }}>
                      {user.skills ? user.skills.split(',').map((s, idx) => (
                        <span className="badge badge-primary" key={idx}>{s.trim()}</span>
                      )) : <span style={{ color: 'var(--text-secondary)' }}>No skills listed yet.</span>}
                    </div>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Education:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px' }}>
                      {user.college ? `${user.college} ${user.graduationYear ? `(Graduated: ${user.graduationYear})` : ''}` : 'No college set.'}
                    </p>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '14px', whiteSpace: 'pre-line' }}>{user.education}</p>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Experience:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px', whiteSpace: 'pre-line' }}>{user.experience || 'No experience details added.'}</p>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px', display: 'flex', gap: '20px' }}>
                    {user.githubUrl && <a href={user.githubUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>GitHub Profile</a>}
                    {user.linkedinUrl && <a href={user.linkedinUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>LinkedIn Profile</a>}
                  </div>

                  {/* Resume Section */}
                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '20px', marginTop: '10px' }}>
                    <h3>Resume Management</h3>
                    {resume ? (
                      <div>
                        <div style={{ background: 'rgba(255,255,255,0.03)', padding: '15px', borderRadius: '8px', border: '1px solid var(--glass-border)', display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                          <div>
                            <strong>{resume.fileName}</strong>
                            <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>
                              Uploaded: {new Date(resume.uploadedAt).toLocaleDateString()}
                            </div>
                          </div>
                          <div style={{ display: 'flex', gap: '10px' }}>
                            <button 
                              onClick={handleAnalyzeResume}
                              disabled={isAnalyzing}
                              className="search-btn"
                              style={{ background: 'var(--primary)', padding: '8px 15px', fontSize: '14px' }}
                            >
                              {isAnalyzing ? 'Analyzing...' : 'Analyze Resume'}
                            </button>
                            <a 
                              href={`${API_BASE}/resumes/download/${user.id}`} 
                              target="_blank" 
                              rel="noreferrer"
                              className="search-btn"
                              style={{ background: 'var(--info)', padding: '8px 15px', fontSize: '14px', textDecoration: 'none' }}
                            >
                              Download
                            </a>
                            <button 
                              onClick={handleResumeDelete}
                              className="search-btn"
                              style={{ background: 'var(--danger)', padding: '8px 15px', fontSize: '14px' }}
                            >
                              Delete
                            </button>
                          </div>
                        </div>

                        {/* AI Resume Analysis Report UI */}
                        {analysisError && (
                          <div style={{ marginTop: '15px', padding: '12px', borderRadius: '6px', background: 'rgba(239, 68, 68, 0.15)', border: '1px solid #ef4444', color: '#f87171', fontSize: '13px' }}>
                            {analysisError}
                          </div>
                        )}

                        {resumeAnalysis && (
                          <div style={{ marginTop: '20px', display: 'flex', flexDirection: 'column', gap: '15px' }}>
                            <h3 style={{ borderBottom: '1px solid var(--glass-border)', paddingBottom: '8px', color: '#a78bfa' }}>
                              ✨ AI Resume Analysis Report
                            </h3>
                            
                            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '15px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                              <strong style={{ color: '#c084fc', fontSize: '14px' }}>Candidate Name:</strong>
                              <p style={{ margin: '4px 0 0 0', color: '#f3f4f6' }}>{resumeAnalysis.candidateName}</p>
                            </div>

                            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '15px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                              <strong style={{ color: '#c084fc', fontSize: '14px' }}>Professional Summary:</strong>
                              <p style={{ margin: '4px 0 0 0', color: '#d1d5db', fontSize: '14px', lineHeight: '1.5' }}>{resumeAnalysis.summary}</p>
                            </div>

                            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(220px, 1fr))', gap: '15px' }}>
                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Skills:</strong>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                  {resumeAnalysis.skills && resumeAnalysis.skills.length > 0 ? resumeAnalysis.skills.map((item, idx) => (
                                    <span key={idx} className="badge badge-primary">{item}</span>
                                  )) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>Not specified</span>}
                                </div>
                              </div>

                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Programming Languages:</strong>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                  {resumeAnalysis.programmingLanguages && resumeAnalysis.programmingLanguages.length > 0 ? resumeAnalysis.programmingLanguages.map((item, idx) => (
                                    <span key={idx} className="badge">{item}</span>
                                  )) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>Not specified</span>}
                                </div>
                              </div>

                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Frameworks:</strong>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                  {resumeAnalysis.frameworks && resumeAnalysis.frameworks.length > 0 ? resumeAnalysis.frameworks.map((item, idx) => (
                                    <span key={idx} className="badge">{item}</span>
                                  )) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>Not specified</span>}
                                </div>
                              </div>

                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Databases:</strong>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                  {resumeAnalysis.databases && resumeAnalysis.databases.length > 0 ? resumeAnalysis.databases.map((item, idx) => (
                                    <span key={idx} className="badge">{item}</span>
                                  )) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>Not specified</span>}
                                </div>
                              </div>

                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Tools:</strong>
                                <div style={{ display: 'flex', flexWrap: 'wrap', gap: '6px', marginTop: '6px' }}>
                                  {resumeAnalysis.tools && resumeAnalysis.tools.length > 0 ? resumeAnalysis.tools.map((item, idx) => (
                                    <span key={idx} className="badge">{item}</span>
                                  )) : <span style={{ color: '#9ca3af', fontSize: '12px' }}>Not specified</span>}
                                </div>
                              </div>

                              <div style={{ background: 'rgba(255,255,255,0.02)', padding: '12px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                                <strong style={{ color: '#818cf8', fontSize: '13px' }}>Years of Experience:</strong>
                                <p style={{ margin: '4px 0 0 0', color: '#f3f4f6', fontWeight: 'bold' }}>
                                  {resumeAnalysis.yearsOfExperience !== null && resumeAnalysis.yearsOfExperience !== undefined ? `${resumeAnalysis.yearsOfExperience} Years` : 'Not specified'}
                                </p>
                              </div>
                            </div>

                            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '15px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                              <strong style={{ color: '#34d399', fontSize: '14px' }}>Key Strengths:</strong>
                              <ul style={{ margin: '6px 0 0 20px', padding: 0, color: '#d1d5db', fontSize: '13px' }}>
                                {resumeAnalysis.strengths && resumeAnalysis.strengths.length > 0 ? resumeAnalysis.strengths.map((item, idx) => (
                                  <li key={idx}>{item}</li>
                                )) : <li>Not specified</li>}
                              </ul>
                            </div>

                            <div style={{ background: 'rgba(255,255,255,0.02)', padding: '15px', borderRadius: '8px', border: '1px solid var(--glass-border)' }}>
                              <strong style={{ color: '#f87171', fontSize: '14px' }}>Areas to Improve:</strong>
                              <ul style={{ margin: '6px 0 0 20px', padding: 0, color: '#d1d5db', fontSize: '13px' }}>
                                {resumeAnalysis.improvementAreas && resumeAnalysis.improvementAreas.length > 0 ? resumeAnalysis.improvementAreas.map((item, idx) => (
                                  <li key={idx}>{item}</li>
                                )) : <li>Not specified</li>}
                              </ul>
                            </div>
                          </div>
                        )}
                      </div>
                    ) : (
                      <form onSubmit={handleResumeUpload} style={{ display: 'flex', gap: '15px', alignItems: 'center' }}>
                        <input 
                          type="file" 
                          accept="application/pdf"
                          onChange={(e) => setResumeFile(e.target.files[0])}
                          className="form-input"
                          style={{ flex: 1 }}
                        />
                        <button type="submit" className="search-btn">Upload PDF</button>
                      </form>
                    )}
                    {resumeUploadStatus && <p style={{ fontSize: '14px', color: 'var(--primary)', marginTop: '10px' }}>{resumeUploadStatus}</p>}
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'company-profile' && user && user.role === 'RECRUITER' && (
            <div className="job-card">
              <h2>Company Profile</h2>
              {companies.length === 0 ? (
                <div>
                  <p>No company profile associated yet.</p>
                  <button className="search-btn" onClick={handleCreateCompany}>Create Company</button>
                </div>
              ) : (
                <form onSubmit={handleSaveCompany}>
                  <div className="form-group">
                    <label className="form-label">Company Name</label>
                    <input type="text" className="form-input" required value={compName} onChange={(e) => setCompName(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Industry</label>
                    <input type="text" className="form-input" placeholder="e.g. Technology, Healthcare" value={compIndustry} onChange={(e) => setCompIndustry(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Website</label>
                    <input type="url" className="form-input" placeholder="https://example.com" value={compWebsite} onChange={(e) => setCompWebsite(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Location</label>
                    <input type="text" className="form-input" placeholder="e.g. San Francisco, CA" value={compLoc} onChange={(e) => setCompLoc(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Company Description</label>
                    <textarea className="form-textarea" rows="4" value={compDesc} onChange={(e) => setCompDesc(e.target.value)}></textarea>
                  </div>
                  <button type="submit" className="search-btn">Save Company Details</button>
                </form>
              )}
            </div>
          )}

          {activeTab === 'recruiter-assistant' && user && user.role === 'RECRUITER' && (
            <RecruiterAssistant
              recruiterJobs={recruiterJobs}
              token={token}
              API_BASE={API_BASE}
              onViewCandidate={(candId, jobId) => {
                const targetJob = recruiterJobs.find(j => j.id === Number(jobId));
                if (targetJob) {
                  setSelectedApplicantJob(targetJob);
                  fetchJobApplicants(targetJob.id);
                  setActiveTab('dashboard');
                }
              }}
            />
          )}

          {activeTab === 'dashboard' && user && user.role === 'RECRUITER' && (
            <div>
              <h2>Applicants Management</h2>
              {selectedCandidate ? (
                <div className="job-card" style={{ textAlign: 'left' }}>
                  <button className="nav-btn" onClick={() => setSelectedCandidate(null)} style={{ marginBottom: '15px' }}>
                    &larr; Back to Applicants List
                  </button>
                  <h3 style={{ fontSize: '24px', margin: '0 0 5px 0' }}>{selectedCandidate.name}</h3>
                  <p style={{ color: 'var(--primary)', fontWeight: 'bold' }}>{selectedCandidate.title || 'No title'}</p>
                  <p style={{ color: 'var(--text-secondary)' }}>{selectedCandidate.location || 'No location'} | {selectedCandidate.phone || 'No phone'}</p>
                  
                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px', marginTop: '15px' }}>
                    <strong>Professional Summary:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px', whiteSpace: 'pre-line' }}>{selectedCandidate.bio || 'No summary added.'}</p>
                  </div>
                  
                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Skills:</strong>
                    <div style={{ display: 'flex', gap: '8px', flexWrap: 'wrap', marginTop: '8px' }}>
                      {selectedCandidate.skills ? selectedCandidate.skills.split(',').map((s, idx) => (
                        <span className="badge badge-primary" key={idx}>{s.trim()}</span>
                      )) : <span style={{ color: 'var(--text-secondary)' }}>No skills listed.</span>}
                    </div>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Education:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px' }}>
                      {selectedCandidate.college ? `${selectedCandidate.college} ${selectedCandidate.graduationYear ? `(Graduated: ${selectedCandidate.graduationYear})` : ''}` : 'No college set.'}
                    </p>
                    <p style={{ color: 'var(--text-secondary)', fontSize: '14px', whiteSpace: 'pre-line' }}>{selectedCandidate.education}</p>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px' }}>
                    <strong>Experience:</strong>
                    <p style={{ color: 'var(--text-secondary)', marginTop: '5px', whiteSpace: 'pre-line' }}>{selectedCandidate.experience || 'No experience details.'}</p>
                  </div>

                  <div style={{ borderTop: '1px solid var(--glass-border)', paddingTop: '15px', display: 'flex', gap: '20px' }}>
                    {selectedCandidate.githubUrl && <a href={selectedCandidate.githubUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>GitHub Profile</a>}
                    {selectedCandidate.linkedinUrl && <a href={selectedCandidate.linkedinUrl} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>LinkedIn Profile</a>}
                  </div>
                </div>
              ) : selectedApplicantJob ? (
                <div>
                  <button className="nav-btn" onClick={() => setSelectedApplicantJob(null)} style={{ marginBottom: '15px' }}>
                    &larr; Back to Job List
                  </button>
                  <h3>Candidates for: {selectedApplicantJob.title}</h3>
                  {jobApplicants.length === 0 ? (
                    <p>No applicants yet.</p>
                  ) : (
                    <div className="jobs-list">
                      {jobApplicants.map((app) => (
                        <div className="job-card" key={app.id}>
                          <div className="job-header">
                            <div>
                              <h3 
                                className="job-title" 
                                style={{ cursor: 'pointer', color: 'var(--primary)', textDecoration: 'underline' }}
                                onClick={() => fetchCandidateProfile(app.applicantId)}
                              >
                                {app.applicantName}
                              </h3>
                              <span className="job-company">{app.applicantEmail}</span>
                            </div>
                            <div>
                              <select 
                                className="form-select" 
                                value={app.status} 
                                onChange={(e) => updateApplicationStatus(app.id, e.target.value)}
                                style={{ width: 'auto', display: 'inline-block' }}
                              >
                                <option value="APPLIED">APPLIED</option>
                                <option value="SHORTLISTED">SHORTLISTED</option>
                                <option value="INTERVIEW">INTERVIEW</option>
                                <option value="HIRED">HIRED</option>
                                <option value="REJECTED">REJECTED</option>
                              </select>
                            </div>
                          </div>
                          <p className="job-desc"><strong>Cover Letter:</strong> {app.coverLetter}</p>
                          <div style={{ marginTop: '10px' }}>
                            <a 
                              href={`${API_BASE}/resumes/download/${app.applicantId}`} 
                              target="_blank" 
                              rel="noreferrer"
                              className="nav-btn"
                              style={{ color: 'var(--primary)', textDecoration: 'underline' }}
                            >
                              Download Resume
                            </a>
                          </div>
                        </div>
                      ))}
                    </div>
                  )}
                </div>
              ) : (
                <div>
                  <h3>Select a job listing to view candidates:</h3>
                  <div className="jobs-list">
                    {recruiterJobs.map(job => (
                      <div className="job-card" key={job.id} style={{ cursor: 'pointer' }} onClick={() => { setSelectedApplicantJob(job); fetchApplicantsForJob(job.id); }}>
                        <h4 style={{ margin: '0 0 5px 0', fontSize: '18px' }}>{job.title}</h4>
                        <span style={{ color: 'var(--text-secondary)', fontSize: '14px' }}>Location: {job.location}</span>
                      </div>
                    ))}
                  </div>
                </div>
              )}
            </div>
          )}

          {activeTab === 'post-job' && user && user.role === 'RECRUITER' && (
            <div className="job-card">
              <h2>Post a New Job</h2>
              <form onSubmit={handleCreateJob}>
                <div className="form-group">
                  <label className="form-label">Job Title</label>
                  <input type="text" className="form-input" required value={newJobTitle} onChange={(e) => setNewJobTitle(e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">Select Company</label>
                  <select className="form-select" value={selectedCompanyId} onChange={(e) => setSelectedCompanyId(e.target.value)}>
                    {companies.map(c => (
                      <option key={c.id} value={c.id}>{c.name}</option>
                    ))}
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Location</label>
                  <input type="text" className="form-input" required value={newJobLoc} onChange={(e) => setNewJobLoc(e.target.value)} />
                </div>
                <div className="form-group">
                  <label className="form-label">Job Type</label>
                  <select className="form-select" value={newJobType} onChange={(e) => setNewJobType(e.target.value)}>
                    <option value="FULL_TIME">Full Time</option>
                    <option value="PART_TIME">Part Time</option>
                    <option value="CONTRACT">Contract</option>
                    <option value="REMOTE">Remote</option>
                    <option value="HYBRID">Hybrid</option>
                  </select>
                </div>
                <div className="form-group">
                  <label className="form-label">Experience Level</label>
                  <input type="text" className="form-input" placeholder="e.g. 2+ years, Senior" value={newJobExp} onChange={(e) => setNewJobExp(e.target.value)} />
                </div>
                <div style={{ display: 'grid', gridTemplateColumns: '1fr 1fr', gap: '20px' }}>
                  <div className="form-group">
                    <label className="form-label">Min Salary ($)</label>
                    <input type="number" className="form-input" value={newJobSalaryMin} onChange={(e) => setNewJobSalaryMin(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Max Salary ($)</label>
                    <input type="number" className="form-input" value={newJobSalaryMax} onChange={(e) => setNewJobSalaryMax(e.target.value)} />
                  </div>
                </div>
                <div className="form-group">
                  <label className="form-label">Description</label>
                  <textarea rows="5" className="form-textarea" required value={newJobDesc} onChange={(e) => setNewJobDesc(e.target.value)}></textarea>
                </div>
                <button type="submit" className="search-btn">Publish Job</button>
              </form>
            </div>
          )}

          {/* Admin Dashboard */}
          {activeTab === 'admin-dashboard' && user && user.role === 'ADMIN' && (
            <div>
              <h2>Admin Overview Dashboard</h2>
              {stats && (
                <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fit, minmax(180px, 1fr))', gap: '15px', marginBottom: '25px' }}>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalCandidates || stats.totalJobSeekers}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Candidates</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalRecruiters}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Recruiters</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalCompanies}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Companies</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalJobs}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Jobs</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.activeJobsCount || stats.activeJobs}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Active Jobs</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalApplications}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Total Applications</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.shortlistedApplications || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Shortlisted</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalInterviews || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Interviews</div>
                  </div>
                  <div className="job-card" style={{ textAlign: 'center', padding: '15px' }}>
                    <div style={{ fontSize: '28px', fontWeight: 'bold', color: 'var(--primary)' }}>{stats.totalHired || 0}</div>
                    <div style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Hired Candidates</div>
                  </div>
                </div>
              )}
            </div>
          )}

          {/* Admin Users / Recruiters / Candidates */}
          {(activeTab === 'admin-users' || activeTab === 'admin-recruiters' || activeTab === 'admin-candidates') && user && user.role === 'ADMIN' && (
            <div className="job-card">
              <h2>User Management {activeTab === 'admin-recruiters' ? '(Recruiters)' : activeTab === 'admin-candidates' ? '(Candidates)' : ''}</h2>
              <div style={{ display: 'flex', gap: '15px', marginBottom: '20px', flexWrap: 'wrap' }}>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="Search by name or email..." 
                  value={adminUserSearch} 
                  onChange={(e) => { setAdminUserSearch(e.target.value); setAdminUserPage(1); }} 
                  style={{ flex: 1, minWidth: '200px' }}
                />
                {activeTab === 'admin-users' && (
                  <select 
                    className="form-select" 
                    value={adminRoleFilter} 
                    onChange={(e) => { setAdminRoleFilter(e.target.value); setAdminUserPage(1); }} 
                    style={{ width: '180px' }}
                  >
                    <option value="">All Roles</option>
                    <option value="JOB_SEEKER">Candidate</option>
                    <option value="RECRUITER">Recruiter</option>
                    <option value="ADMIN">Admin</option>
                  </select>
                )}
              </div>

              {(() => {
                let filtered = adminUsers.filter(u => {
                  if (activeTab === 'admin-recruiters' && u.role !== 'RECRUITER') return false;
                  if (activeTab === 'admin-candidates' && u.role !== 'JOB_SEEKER') return false;
                  if (adminRoleFilter && u.role !== adminRoleFilter) return false;
                  if (adminUserSearch) {
                    const q = adminUserSearch.toLowerCase();
                    return u.name.toLowerCase().includes(q) || u.email.toLowerCase().includes(q);
                  }
                  return true;
                });

                const pageSize = 5;
                const totalPages = Math.ceil(filtered.length / pageSize) || 1;
                const paged = filtered.slice((adminUserPage - 1) * pageSize, adminUserPage * pageSize);

                return (
                  <div>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                      <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                          <th style={{ padding: '10px' }}>Name</th>
                          <th style={{ padding: '10px' }}>Email</th>
                          <th style={{ padding: '10px' }}>Role</th>
                          <th style={{ padding: '10px' }}>Status</th>
                          <th style={{ padding: '10px' }}>Created Date</th>
                          <th style={{ padding: '10px' }}>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {paged.map(u => (
                          <tr key={u.id} style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <td style={{ padding: '10px', fontWeight: 'bold' }}>{u.name}</td>
                            <td style={{ padding: '10px' }}>{u.email}</td>
                            <td style={{ padding: '10px' }}><span className="badge badge-primary">{u.role}</span></td>
                            <td style={{ padding: '10px' }}>
                              <span style={{ color: u.status === 'BLOCKED' ? 'var(--danger)' : '#4ade80', fontWeight: 'bold' }}>
                                {u.status || 'ACTIVE'}
                              </span>
                            </td>
                            <td style={{ padding: '10px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                              {new Date(u.createdAt).toLocaleDateString()}
                            </td>
                            <td style={{ padding: '10px' }}>
                              {u.role !== 'ADMIN' && (
                                <button 
                                  className="search-btn"
                                  style={{ 
                                    background: u.status === 'BLOCKED' ? '#4ade80' : 'var(--danger)', 
                                    padding: '5px 10px', 
                                    fontSize: '12px' 
                                  }}
                                  onClick={() => toggleUserBlockStatus(u.id, u.status || 'ACTIVE')}
                                >
                                  {u.status === 'BLOCKED' ? 'Unblock' : 'Block'}
                                </button>
                              )}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>
                    
                    {/* Pagination */}
                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '15px' }}>
                      <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Showing Page {adminUserPage} of {totalPages} ({filtered.length} total)</span>
                      <div style={{ display: 'flex', gap: '5px' }}>
                        <button className="nav-btn" disabled={adminUserPage <= 1} onClick={() => setAdminUserPage(p => p - 1)}>&laquo; Prev</button>
                        <button className="nav-btn" disabled={adminUserPage >= totalPages} onClick={() => setAdminUserPage(p => p + 1)}>Next &raquo;</button>
                      </div>
                    </div>
                  </div>
                );
              })()}
            </div>
          )}

          {/* Admin Companies */}
          {activeTab === 'admin-companies' && user && user.role === 'ADMIN' && (
            <div className="job-card">
              <h2>Company Management</h2>
              <div style={{ marginBottom: '20px' }}>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="Search company by name, industry, or location..." 
                  value={adminCompanySearch} 
                  onChange={(e) => { setAdminCompanySearch(e.target.value); setAdminCompanyPage(1); }} 
                />
              </div>

              {(() => {
                let filtered = adminCompanies.filter(c => {
                  if (adminCompanySearch) {
                    const q = adminCompanySearch.toLowerCase();
                    return c.name.toLowerCase().includes(q) || 
                           (c.industry && c.industry.toLowerCase().includes(q)) ||
                           (c.location && c.location.toLowerCase().includes(q));
                  }
                  return true;
                });

                const pageSize = 5;
                const totalPages = Math.ceil(filtered.length / pageSize) || 1;
                const paged = filtered.slice((adminCompanyPage - 1) * pageSize, adminCompanyPage * pageSize);

                return (
                  <div>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                      <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                          <th style={{ padding: '10px' }}>Company Name</th>
                          <th style={{ padding: '10px' }}>Industry</th>
                          <th style={{ padding: '10px' }}>Location</th>
                          <th style={{ padding: '10px' }}>Website</th>
                          <th style={{ padding: '10px' }}>Recruiter</th>
                          <th style={{ padding: '10px' }}>Total Jobs</th>
                        </tr>
                      </thead>
                      <tbody>
                        {paged.map(c => (
                          <tr key={c.id} style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <td style={{ padding: '10px', fontWeight: 'bold' }}>{c.name}</td>
                            <td style={{ padding: '10px' }}>{c.industry || 'N/A'}</td>
                            <td style={{ padding: '10px' }}>{c.location || 'N/A'}</td>
                            <td style={{ padding: '10px' }}>
                              {c.website ? <a href={c.website} target="_blank" rel="noreferrer" style={{ color: 'var(--primary)' }}>Visit</a> : 'N/A'}
                            </td>
                            <td style={{ padding: '10px' }}>{c.recruiterName || 'N/A'}</td>
                            <td style={{ padding: '10px' }}>{c.jobCount || 0}</td>
                          </tr>
                        ))}
                      </tbody>
                    </table>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '15px' }}>
                      <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Showing Page {adminCompanyPage} of {totalPages} ({filtered.length} total)</span>
                      <div style={{ display: 'flex', gap: '5px' }}>
                        <button className="nav-btn" disabled={adminCompanyPage <= 1} onClick={() => setAdminCompanyPage(p => p - 1)}>&laquo; Prev</button>
                        <button className="nav-btn" disabled={adminCompanyPage >= totalPages} onClick={() => setAdminCompanyPage(p => p + 1)}>Next &raquo;</button>
                      </div>
                    </div>
                  </div>
                );
              })()}
            </div>
          )}

          {/* Admin Jobs */}
          {activeTab === 'admin-jobs' && user && user.role === 'ADMIN' && (
            <div className="job-card">
              <h2>Job Management</h2>
              <div style={{ display: 'flex', gap: '15px', marginBottom: '20px', flexWrap: 'wrap' }}>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="Search job title, company, or location..." 
                  value={adminJobSearch} 
                  onChange={(e) => { setAdminJobSearch(e.target.value); setAdminJobPage(1); }} 
                  style={{ flex: 1, minWidth: '200px' }}
                />
                <select 
                  className="form-select" 
                  value={adminJobStatusFilter} 
                  onChange={(e) => { setAdminJobStatusFilter(e.target.value); setAdminJobPage(1); }} 
                  style={{ width: '180px' }}
                >
                  <option value="">All Statuses</option>
                  <option value="ACTIVE">ACTIVE</option>
                  <option value="CLOSED">CLOSED</option>
                </select>
              </div>

              {(() => {
                let filtered = adminJobs.filter(j => {
                  if (adminJobStatusFilter && j.status !== adminJobStatusFilter) return false;
                  if (adminJobSearch) {
                    const q = adminJobSearch.toLowerCase();
                    return j.title.toLowerCase().includes(q) || 
                           (j.companyName && j.companyName.toLowerCase().includes(q)) ||
                           (j.location && j.location.toLowerCase().includes(q));
                  }
                  return true;
                });

                const pageSize = 5;
                const totalPages = Math.ceil(filtered.length / pageSize) || 1;
                const paged = filtered.slice((adminJobPage - 1) * pageSize, adminJobPage * pageSize);

                return (
                  <div>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                      <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                          <th style={{ padding: '10px' }}>Job Title</th>
                          <th style={{ padding: '10px' }}>Company</th>
                          <th style={{ padding: '10px' }}>Recruiter</th>
                          <th style={{ padding: '10px' }}>Location</th>
                          <th style={{ padding: '10px' }}>Type</th>
                          <th style={{ padding: '10px' }}>Status</th>
                          <th style={{ padding: '10px' }}>Apps</th>
                          <th style={{ padding: '10px' }}>Action</th>
                        </tr>
                      </thead>
                      <tbody>
                        {paged.map(j => (
                          <tr key={j.id} style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <td style={{ padding: '10px', fontWeight: 'bold' }}>{j.title}</td>
                            <td style={{ padding: '10px' }}>{j.companyName}</td>
                            <td style={{ padding: '10px' }}>{j.postedByName}</td>
                            <td style={{ padding: '10px' }}>{j.location}</td>
                            <td style={{ padding: '10px' }}><span className="badge badge-primary">{j.jobType}</span></td>
                            <td style={{ padding: '10px' }}>
                              <span style={{ color: j.status === 'CLOSED' ? 'var(--danger)' : '#4ade80', fontWeight: 'bold' }}>
                                {j.status}
                              </span>
                            </td>
                            <td style={{ padding: '10px' }}>{j.applicationCount || 0}</td>
                            <td style={{ padding: '10px' }}>
                              {j.status === 'ACTIVE' ? (
                                <button 
                                  className="search-btn"
                                  style={{ background: 'var(--danger)', padding: '5px 10px', fontSize: '12px' }}
                                  onClick={() => updateAdminJobStatus(j.id, 'CLOSED')}
                                >
                                  Close
                                </button>
                              ) : (
                                <button 
                                  className="search-btn"
                                  style={{ background: '#4ade80', padding: '5px 10px', fontSize: '12px' }}
                                  onClick={() => updateAdminJobStatus(j.id, 'ACTIVE')}
                                >
                                  Activate
                                </button>
                              )}
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '15px' }}>
                      <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Showing Page {adminJobPage} of {totalPages} ({filtered.length} total)</span>
                      <div style={{ display: 'flex', gap: '5px' }}>
                        <button className="nav-btn" disabled={adminJobPage <= 1} onClick={() => setAdminJobPage(p => p - 1)}>&laquo; Prev</button>
                        <button className="nav-btn" disabled={adminJobPage >= totalPages} onClick={() => setAdminJobPage(p => p + 1)}>Next &raquo;</button>
                      </div>
                    </div>
                  </div>
                );
              })()}
            </div>
          )}

          {/* Admin Applications */}
          {activeTab === 'admin-applications' && user && user.role === 'ADMIN' && (
            <div className="job-card">
              <h2>Application Management</h2>
              <div style={{ display: 'flex', gap: '15px', marginBottom: '20px', flexWrap: 'wrap' }}>
                <input 
                  type="text" 
                  className="form-input" 
                  placeholder="Search candidate name, email, or job title..." 
                  value={adminAppSearch} 
                  onChange={(e) => { setAdminAppSearch(e.target.value); setAdminAppPage(1); }} 
                  style={{ flex: 1, minWidth: '200px' }}
                />
                <select 
                  className="form-select" 
                  value={adminAppStatusFilter} 
                  onChange={(e) => { setAdminAppStatusFilter(e.target.value); setAdminAppPage(1); }} 
                  style={{ width: '180px' }}
                >
                  <option value="">All Statuses</option>
                  <option value="APPLIED">APPLIED</option>
                  <option value="SHORTLISTED">SHORTLISTED</option>
                  <option value="INTERVIEW">INTERVIEW</option>
                  <option value="HIRED">HIRED</option>
                  <option value="REJECTED">REJECTED</option>
                </select>
              </div>

              {(() => {
                let filtered = adminApplications.filter(a => {
                  if (adminAppStatusFilter && a.status !== adminAppStatusFilter) return false;
                  if (adminAppSearch) {
                    const q = adminAppSearch.toLowerCase();
                    return a.applicantName.toLowerCase().includes(q) || 
                           a.applicantEmail.toLowerCase().includes(q) ||
                           a.jobTitle.toLowerCase().includes(q);
                  }
                  return true;
                });

                const pageSize = 5;
                const totalPages = Math.ceil(filtered.length / pageSize) || 1;
                const paged = filtered.slice((adminAppPage - 1) * pageSize, adminAppPage * pageSize);

                return (
                  <div>
                    <table style={{ width: '100%', borderCollapse: 'collapse', textAlign: 'left' }}>
                      <thead>
                        <tr style={{ borderBottom: '1px solid var(--glass-border)' }}>
                          <th style={{ padding: '10px' }}>Candidate</th>
                          <th style={{ padding: '10px' }}>Job</th>
                          <th style={{ padding: '10px' }}>Company</th>
                          <th style={{ padding: '10px' }}>Recruiter</th>
                          <th style={{ padding: '10px' }}>Applied Date</th>
                          <th style={{ padding: '10px' }}>Status</th>
                          <th style={{ padding: '10px' }}>Resume</th>
                        </tr>
                      </thead>
                      <tbody>
                        {paged.map(a => (
                          <tr key={a.id} style={{ borderBottom: '1px solid var(--glass-border)' }}>
                            <td style={{ padding: '10px' }}>
                              <div style={{ fontWeight: 'bold' }}>{a.applicantName}</div>
                              <div style={{ fontSize: '12px', color: 'var(--text-secondary)' }}>{a.applicantEmail}</div>
                            </td>
                            <td style={{ padding: '10px', fontWeight: 'bold' }}>{a.jobTitle}</td>
                            <td style={{ padding: '10px' }}>{a.companyName}</td>
                            <td style={{ padding: '10px' }}>{a.recruiterName || 'N/A'}</td>
                            <td style={{ padding: '10px', fontSize: '13px', color: 'var(--text-secondary)' }}>
                              {new Date(a.createdAt).toLocaleDateString()}
                            </td>
                            <td style={{ padding: '10px' }}>
                              <span className={getStatusClass(a.status)}>{a.status}</span>
                            </td>
                            <td style={{ padding: '10px' }}>
                              <a 
                                href={`${API_BASE}/resumes/download/${a.applicantId}`} 
                                target="_blank" 
                                rel="noreferrer"
                                style={{ color: 'var(--primary)', textDecoration: 'underline', fontSize: '13px' }}
                              >
                                Resume
                              </a>
                            </td>
                          </tr>
                        ))}
                      </tbody>
                    </table>

                    <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', marginTop: '15px' }}>
                      <span style={{ fontSize: '13px', color: 'var(--text-secondary)' }}>Showing Page {adminAppPage} of {totalPages} ({filtered.length} total)</span>
                      <div style={{ display: 'flex', gap: '5px' }}>
                        <button className="nav-btn" disabled={adminAppPage <= 1} onClick={() => setAdminAppPage(p => p - 1)}>&laquo; Prev</button>
                        <button className="nav-btn" disabled={adminAppPage >= totalPages} onClick={() => setAdminAppPage(p => p + 1)}>Next &raquo;</button>
                      </div>
                    </div>
                  </div>
                );
              })()}
            </div>
          )}
        </main>

        {/* Sidebar */}
        <aside>
          {user && user.role === 'JOB_SEEKER' && (
            <div className="sidebar-widget" style={{ marginBottom: '20px' }}>
              <h3 className="widget-title">Profile Completion</h3>
              <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
                <div style={{ flex: 1, background: 'rgba(255,255,255,0.05)', borderRadius: '10px', height: '15px', overflow: 'hidden' }}>
                  <div style={{ background: 'var(--primary)', width: `${calculateProfileCompletion()}%`, height: '100%' }}></div>
                </div>
                <span style={{ fontWeight: 'bold' }}>{calculateProfileCompletion()}%</span>
              </div>
              <div style={{ display: 'flex', flexDirection: 'column', gap: '10px', marginTop: '20px' }}>
                <button className="nav-btn" onClick={() => setActiveTab('jobs')}>View Jobs</button>
                <button className="nav-btn" onClick={() => setActiveTab('dashboard')}>My Applications</button>
                <button className="nav-btn" onClick={() => setActiveTab('profile')}>My Profile</button>
              </div>
            </div>
          )}

          {stats ? (
            <div className="sidebar-widget">
              <h3 className="widget-title">Your Activity Stats</h3>
              {user.role === 'JOB_SEEKER' ? (
                <>
                  <div className="stat-item">
                    <span className="stat-label">Applications</span>
                    <span className="stat-value">{stats.totalApplications}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Shortlisted</span>
                    <span className="stat-value">{stats.shortlisted}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Interviews</span>
                    <span className="stat-value">{stats.interviews}</span>
                  </div>
                </>
              ) : user.role === 'RECRUITER' ? (
                <>
                  <div className="stat-item">
                    <span className="stat-label">Total Jobs</span>
                    <span className="stat-value">{stats.totalJobs || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Active Jobs</span>
                    <span className="stat-value">{stats.activeJobs || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Total Applications</span>
                    <span className="stat-value">{stats.totalApplicants || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Shortlisted</span>
                    <span className="stat-value">{stats.shortlisted || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Interviews</span>
                    <span className="stat-value">{stats.interviews || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Hired</span>
                    <span className="stat-value">{stats.hired || 0}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Rejected</span>
                    <span className="stat-value">{stats.rejected || 0}</span>
                  </div>
                </>
              ) : (
                <>
                  <div className="stat-item">
                    <span className="stat-label">Total Users</span>
                    <span className="stat-value">{stats.totalUsers}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Candidates</span>
                    <span className="stat-value">{stats.totalCandidates || stats.totalJobSeekers}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Recruiters</span>
                    <span className="stat-value">{stats.totalRecruiters}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Companies</span>
                    <span className="stat-value">{stats.totalCompanies}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Total Jobs</span>
                    <span className="stat-value">{stats.totalJobs}</span>
                  </div>
                  <div className="stat-item">
                    <span className="stat-label">Applications</span>
                    <span className="stat-value">{stats.totalApplications}</span>
                  </div>
                </>
              )}
            </div>
          ) : (
            <div className="sidebar-widget">
              <h3 className="widget-title">Get Matched with AI</h3>
              <p style={{ color: 'var(--text-secondary)', fontSize: '14px', lineHeight: '1.4' }}>
                Create an account or sign in to build your profile, upload your resume, and let our AI matchmaker suggest the best job postings for your skills.
              </p>
            </div>
          )}
        </aside>
      </div>

      {/* Auth Modal */}
      {showAuthModal && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button className="modal-close" onClick={() => setShowAuthModal(false)}>&times;</button>
            <h2>{isRegister ? 'Create Account' : 'Welcome Back'}</h2>
            <form onSubmit={handleAuth}>
              {isRegister && (
                <>
                  <div className="form-group">
                    <label className="form-label">Full Name</label>
                    <input type="text" className="form-input" required value={authName} onChange={(e) => setAuthName(e.target.value)} />
                  </div>
                  <div className="form-group">
                    <label className="form-label">Register As</label>
                    <select className="form-select" value={authRole} onChange={(e) => setAuthRole(e.target.value)}>
                      <option value="JOB_SEEKER">Job Seeker</option>
                      <option value="RECRUITER">Recruiter</option>
                      <option value="ADMIN">Admin</option>
                    </select>
                  </div>
                </>
              )}
              <div className="form-group">
                <label className="form-label">Email Address</label>
                <input type="email" className="form-input" required value={authEmail} onChange={(e) => setAuthEmail(e.target.value)} />
              </div>
              <div className="form-group">
                <label className="form-label">Password</label>
                <input type="password" className="form-input" required value={authPassword} onChange={(e) => setAuthPassword(e.target.value)} />
              </div>
              <button type="submit" className="search-btn" style={{ width: '100%', padding: '14px' }}>
                {isRegister ? 'Sign Up' : 'Sign In'}
              </button>
              <p style={{ marginTop: '15px', textAlign: 'center', fontSize: '14px', color: 'var(--text-secondary)' }}>
                {isRegister ? 'Already have an account?' : "Don't have an account?"}{' '}
                <span 
                  style={{ color: 'var(--primary)', cursor: 'pointer', fontWeight: 'bold' }} 
                  onClick={() => setIsRegister(!isRegister)}
                >
                  {isRegister ? 'Login' : 'Register'}
                </span>
              </p>
            </form>
          </div>
        </div>
      )}

      {/* Apply Modal */}
      {showApplyModal && selectedJob && (
        <div className="modal-overlay">
          <div className="modal-content">
            <button className="modal-close" onClick={() => setShowApplyModal(false)}>&times;</button>
            <h2>Apply for {selectedJob.title}</h2>
            <p style={{ color: 'var(--text-secondary)', marginBottom: '20px' }}>at {selectedJob.companyName}</p>
            <form onSubmit={handleApply}>
              <div className="form-group">
                <label className="form-label">Cover Letter</label>
                <textarea 
                  rows="6" 
                  className="form-textarea" 
                  placeholder="Explain why you are the best fit for this role..." 
                  value={coverLetter}
                  onChange={(e) => setCoverLetter(e.target.value)}
                  required
                ></textarea>
              </div>
              <button type="submit" className="search-btn" style={{ width: '100%', padding: '14px' }}>
                Submit Application
              </button>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}

export default App;
