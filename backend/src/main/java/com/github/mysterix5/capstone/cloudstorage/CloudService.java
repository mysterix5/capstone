package com.github.mysterix5.capstone.cloudstorage;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CloudService {
    private final CloudRepository cloudRepository;

}
