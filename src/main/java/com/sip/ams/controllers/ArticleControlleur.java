package com.sip.ams.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sip.ams.entities.Article;
import com.sip.ams.entities.Provider;
import com.sip.ams.repositories.ArticleRepository;
import com.sip.ams.repositories.ProviderRepository;
import java.util.List;
import java.util.Optional;

import javax.validation.Valid;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping({ "/articles" })
public class ArticleControlleur {

	private final ArticleRepository articleRepository;
	private final ProviderRepository providerRepository;

	@Autowired
	public ArticleControlleur(ArticleRepository articleRepository, ProviderRepository providerRepository) {
		this.articleRepository = articleRepository;
		this.providerRepository = providerRepository;
	}
	/*
	 * @Autowired private ArticleRepository articleRepository;
	 * 
	 * @Autowired private ProviderRepository providerRepository;
	 */

	@GetMapping("/list")
	public List<Article> getAllArticles() {
		return (List<Article>) articleRepository.findAll();
	}

	@PostMapping("/add/{providerId}")
	Article createArticle(@PathVariable(value = "providerId") Long providerId, @Valid @RequestBody Article article) {

		return providerRepository.findById(providerId).map(provider -> {
			article.setProvider(provider);
			return articleRepository.save(article);
		}).orElseThrow(() -> new IllegalArgumentException("ProviderId" + providerId + " not found"));

	}

	@PutMapping("/update/{providerId}/{articleId}")
	public Article updateArticle(@PathVariable(value = "providerId") Long providerId,
			@PathVariable(value = "articleId") Long articleId, @Valid @RequestBody Article articleRequest) {
		if (!providerRepository.existsById(providerId)) {
			throw new IllegalArgumentException("ProviderId " + providerId + " not found");
		}

		return articleRepository.findById(articleId).map(article -> {
			article.setPrice(articleRequest.getPrice());
			article.setLabel(articleRequest.getLabel());
			article.setPicture(articleRequest.getPicture());
			return articleRepository.save(article);
		}).orElseThrow(() -> new IllegalArgumentException("ArticleId " + articleId + "notfound"));
	}
	@GetMapping("/{articleId}")
	public Article getarticle(@PathVariable Long articleId) {
		Optional<Article> p = articleRepository.findById(articleId);
		return p.get();
	}
	@DeleteMapping("/delete/{articleId}")
	public ResponseEntity<?> deleteArticle(@PathVariable(value = "articleId") Long articleId) {
		return articleRepository.findById(articleId).map(article -> {
			articleRepository.delete(article);
			return ResponseEntity.ok().build();
		}).orElseThrow(() -> new IllegalArgumentException("Article not found with id " + articleId));
	}
}
