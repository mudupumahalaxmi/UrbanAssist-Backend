package com.urbanassist.dto.response;

/**
 * StaffStatsResponse — Aggregated complaint counts for the staff dashboard KPI cards.
 * Returned by GET /api/staff/stats to avoid multiple parallel queue fetches.
 */
public class StaffStatsResponse {
    private long total;
    private long pending;
    private long assigned;
    private long inProgress;
    private long resolved;
    private long rejected;

    public StaffStatsResponse() {
    }

    public StaffStatsResponse(long total, long pending, long assigned, long inProgress, long resolved, long rejected) {
        this.total = total;
        this.pending = pending;
        this.assigned = assigned;
        this.inProgress = inProgress;
        this.resolved = resolved;
        this.rejected = rejected;
    }

    public long getTotal() { return total; }
    public void setTotal(long total) { this.total = total; }

    public long getPending() { return pending; }
    public void setPending(long pending) { this.pending = pending; }

    public long getAssigned() { return assigned; }
    public void setAssigned(long assigned) { this.assigned = assigned; }

    public long getInProgress() { return inProgress; }
    public void setInProgress(long inProgress) { this.inProgress = inProgress; }

    public long getResolved() { return resolved; }
    public void setResolved(long resolved) { this.resolved = resolved; }

    public long getRejected() { return rejected; }
    public void setRejected(long rejected) { this.rejected = rejected; }
}
