package cats.ytmp3.dl.request;

/**
 * User: cats
 * Date: 6/17/13
 * Time: 11:51 PM
 */
public class DownloadRequestResponse {

    private final DownloadRequest request;
    private final boolean accepted;

    public DownloadRequestResponse(final DownloadRequest request, final boolean accepted){
        this.request = request;
        this.accepted = accepted;
    }

    public DownloadRequest getRequest(){
        return request;
    }

    public boolean isAccepted(){
        return accepted;
    }

    public String toString(){
        return String.format("%s (%s)", request, accepted ? "Accepted" : "Declined");
    }
}
