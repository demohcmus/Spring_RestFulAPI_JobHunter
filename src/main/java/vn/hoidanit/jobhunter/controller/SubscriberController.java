package vn.hoidanit.jobhunter.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import vn.hoidanit.jobhunter.domain.Subscriber;
import vn.hoidanit.jobhunter.service.SubscriberService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;
import vn.hoidanit.jobhunter.util.error.IdInvalidException;

@RestController
@RequestMapping("/api/v1")
public class SubscriberController {
    private final SubscriberService subscriberService;

    public SubscriberController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @PostMapping("/subscribers")
    @ApiMessage("Create a subscriber")
    public ResponseEntity<Subscriber> createSubscriber(@Valid @RequestBody Subscriber subscriber)
            throws IdInvalidException {

                // check email
                boolean isEmailExist = this.subscriberService.isEmailExist(subscriber.getEmail());
                if (isEmailExist) {
                    throw new IdInvalidException("Email = " + subscriber.getEmail() + " đã tồn tại!");
                }
        Subscriber newSubscriber = this.subscriberService.createSubscriber(subscriber);
        return ResponseEntity.status(HttpStatus.CREATED).body(newSubscriber);

    }

    @PutMapping("/subscribers")
    @ApiMessage("Update a subscriber")
    public ResponseEntity<Subscriber> update(@RequestBody Subscriber subsRequest)
            throws IdInvalidException {

        // check id
        Subscriber subsDB = this.subscriberService.fetchSubscriberById(subsRequest.getId());
        if (subsDB == null) {
            throw new IdInvalidException("Subscriber id = " + subsRequest.getId() + " không tồn tại!");
        }

        return ResponseEntity.status(HttpStatus.OK).body(this.subscriberService.updateSubscriber(subsDB, subsRequest));

    }
}
