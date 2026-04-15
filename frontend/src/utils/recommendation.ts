export interface UserPreference {
  userId: string
  activityTypes: string[]
  locations: string[]
  timeSlots: string[]
  skillRequirements: string[]
  preferences: {
    minDuration: number
    maxDuration: number
    maxDistance: number
    preferredDays: number[]
  }
  history: {
    joinedActivities: string[]
    likedActivities: string[]
    dislikedActivities: string[]
  }
  scores: {
    typeScore: number
    locationScore: number
    timeScore: number
    skillScore: number
    historyScore: number
  }
}

export interface Activity {
  id: string
  title: string
  description: string
  type: string
  location: string
  startTime: string
  endTime: string
  duration: number
  requiredSkills: string[]
  currentParticipants: number
  maxParticipants: number
  points: number
  difficulty: 'easy' | 'medium' | 'hard'
  tags: string[]
  status: 'open' | 'closed' | 'completed'
}

export interface Recommendation {
  activity: Activity
  score: number
  reasons: string[]
  matchDetails: {
    typeMatch: number
    locationMatch: number
    timeMatch: number
    skillMatch: number
    historyMatch: number
    availability: number
  }
}

export class RecommendationEngine {
  private userPreferences: Map<string, UserPreference> = new Map()
  private activities: Activity[] = []
  private weights = {
    type: 0.25,
    location: 0.20,
    time: 0.20,
    skill: 0.15,
    history: 0.15,
    availability: 0.05
  }

  setUserPreference(userId: string, preference: UserPreference) {
    this.userPreferences.set(userId, preference)
  }

  getUserPreference(userId: string): UserPreference | undefined {
    return this.userPreferences.get(userId)
  }

  setActivities(activities: Activity[]) {
    this.activities = activities.filter(a => a.status === 'open')
  }

  calculateTypeMatch(userTypes: string[], activityType: string): number {
    if (userTypes.length === 0) return 0.5
    return userTypes.includes(activityType) ? 1 : 0
  }

  calculateLocationMatch(userLocations: string[], activityLocation: string, maxDistance: number): number {
    if (userLocations.length === 0) return 0.5
    
    const isNear = userLocations.some(loc => 
      loc === activityLocation || this.isWithinDistance(loc, activityLocation, maxDistance)
    )
    
    return isNear ? 1 : 0
  }

  isWithinDistance(loc1: string, loc2: string, maxDistance: number): boolean {
    return Math.abs(loc1.length - loc2.length) < maxDistance
  }

  calculateTimeMatch(userTimeSlots: string[], activityTime: string): number {
    if (userTimeSlots.length === 0) return 0.5
    
    const activityHour = parseInt(activityTime.split(':')[0])
    const matches = userTimeSlots.filter(slot => {
      const [start, end] = slot.split('-').map(t => parseInt(t))
      return activityHour >= start && activityHour < end
    })
    
    return matches.length > 0 ? 1 : 0
  }

  calculateSkillMatch(userSkills: string[], requiredSkills: string[]): number {
    if (requiredSkills.length === 0) return 1
    if (userSkills.length === 0) return 0
    
    const matchedSkills = requiredSkills.filter(skill => 
      userSkills.includes(skill)
    )
    
    return matchedSkills.length / requiredSkills.length
  }

  calculateHistoryMatch(history: UserPreference['history'], activityId: string): number {
    let score = 0
    
    if (history.likedActivities.includes(activityId)) {
      score += 1
    }
    
    if (history.dislikedActivities.includes(activityId)) {
      score -= 1
    }
    
    const similarActivities = this.findSimilarActivities(activityId, history.joinedActivities)
    score += similarActivities.length * 0.3
    
    return Math.max(0, Math.min(1, score))
  }

  findSimilarActivities(activityId: string, joinedActivities: string[]): string[] {
    const activity = this.activities.find(a => a.id === activityId)
    if (!activity) return []
    
    return joinedActivities.filter(id => {
      const joinedActivity = this.activities.find(a => a.id === id)
      if (!joinedActivity) return false
      
      const typeMatch = activity.type === joinedActivity.type
      const tagMatch = activity.tags.some(tag => joinedActivity.tags.includes(tag))
      
      return typeMatch || tagMatch
    })
  }

  calculateAvailability(activity: Activity): number {
    const availability = (activity.maxParticipants - activity.currentParticipants) / activity.maxParticipants
    return Math.min(1, availability * 2)
  }

  generateRecommendations(userId: string, limit: number = 10): Recommendation[] {
    const preference = this.userPreferences.get(userId)
    if (!preference) {
      return this.getFallbackRecommendations(limit)
    }

    const recommendations: Recommendation[] = this.activities.map(activity => {
      const matchDetails = {
        typeMatch: this.calculateTypeMatch(preference.activityTypes, activity.type),
        locationMatch: this.calculateLocationMatch(
          preference.locations,
          activity.location,
          preference.preferences.maxDistance
        ),
        timeMatch: this.calculateTimeMatch(preference.timeSlots, activity.startTime),
        skillMatch: this.calculateSkillMatch(preference.skillRequirements, activity.requiredSkills),
        historyMatch: this.calculateHistoryMatch(preference.history, activity.id),
        availability: this.calculateAvailability(activity)
      }

      const score = 
        matchDetails.typeMatch * this.weights.type +
        matchDetails.locationMatch * this.weights.location +
        matchDetails.timeMatch * this.weights.time +
        matchDetails.skillMatch * this.weights.skill +
        matchDetails.historyMatch * this.weights.history +
        matchDetails.availability * this.weights.availability

      const reasons = this.generateReasons(matchDetails, activity)

      return {
        activity,
        score: Math.round(score * 100) / 100,
        reasons,
        matchDetails
      }
    })

    return recommendations
      .sort((a, b) => b.score - a.score)
      .slice(0, limit)
  }

  generateReasons(matchDetails: Recommendation['matchDetails'], activity: Activity): string[] {
    const reasons: string[] = []
    
    if (matchDetails.typeMatch > 0.8) {
      reasons.push(`匹配您的活动类型偏好：${activity.type}`)
    }
    
    if (matchDetails.locationMatch > 0.8) {
      reasons.push(`活动地点符合您的区域偏好`)
    }
    
    if (matchDetails.timeMatch > 0.8) {
      reasons.push(`活动时间符合您的时间安排`)
    }
    
    if (matchDetails.skillMatch > 0.8) {
      reasons.push(`您的技能完全符合活动要求`)
    }
    
    if (matchDetails.historyMatch > 0.5) {
      reasons.push(`基于您的参与历史推荐`)
    }
    
    if (matchDetails.availability > 0.8) {
      reasons.push(`活动名额充足`)
    }
    
    return reasons
  }

  getFallbackRecommendations(limit: number): Recommendation[] {
    const recommendations: Recommendation[] = this.activities.map(activity => {
      const score = this.calculatePopularityScore(activity)
      
      return {
        activity,
        score,
        reasons: ['热门活动推荐'],
        matchDetails: {
          typeMatch: 0,
          locationMatch: 0,
          timeMatch: 0,
          skillMatch: 0,
          historyMatch: 0,
          availability: this.calculateAvailability(activity)
        }
      }
    })

    return recommendations
      .sort((a, b) => b.score - a.score)
      .slice(0, limit)
  }

  calculatePopularityScore(activity: Activity): number {
    const participantRatio = activity.currentParticipants / activity.maxParticipants
    const pointsBonus = activity.points / 100
    const difficultyBonus = activity.difficulty === 'easy' ? 0.1 : 0
    
    return participantRatio * 0.5 + pointsBonus * 0.3 + difficultyBonus
  }

  updateUserPreference(userId: string, feedback: {
    activityId: string
    action: 'join' | 'like' | 'dislike' | 'view'
  }) {
    const preference = this.userPreferences.get(userId)
    if (!preference) return

    switch (feedback.action) {
      case 'join':
        preference.history.joinedActivities.push(feedback.activityId)
        break
      case 'like':
        preference.history.likedActivities.push(feedback.activityId)
        break
      case 'dislike':
        preference.history.dislikedActivities.push(feedback.activityId)
        break
      case 'view':
        break
    }

    this.userPreferences.set(userId, preference)
  }

  updateWeights(weights: Partial<typeof this.weights>) {
    this.weights = { ...this.weights, ...weights }
    
    const total = Object.values(this.weights).reduce((sum, w) => sum + w, 0)
    if (total !== 1) {
      Object.keys(this.weights).forEach(key => {
        this.weights[key as keyof typeof this.weights] /= total
      })
    }
  }

  getRecommendationStats(userId: string): {
    totalActivities: number
    recommendedCount: number
    averageScore: number
    topReasons: string[]
  } {
    const recommendations = this.generateRecommendations(userId, 50)
    const averageScore = recommendations.reduce((sum, r) => sum + r.score, 0) / recommendations.length
    
    const reasonCounts = new Map<string, number>()
    recommendations.forEach(r => {
      r.reasons.forEach(reason => {
        reasonCounts.set(reason, (reasonCounts.get(reason) || 0) + 1)
      })
    })
    
    const topReasons = Array.from(reasonCounts.entries())
      .sort((a, b) => b[1] - a[1])
      .slice(0, 5)
      .map(([reason]) => reason)

    return {
      totalActivities: this.activities.length,
      recommendedCount: recommendations.length,
      averageScore: Math.round(averageScore * 100) / 100,
      topReasons
    }
  }
}

export const recommendationEngine = new RecommendationEngine()
