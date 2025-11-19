package br.com.workingsafe.service;

import br.com.workingsafe.dto.PapelDto;
import br.com.workingsafe.mapper.PapelMapper;
import br.com.workingsafe.model.Papel;
import br.com.workingsafe.repository.PapelRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PapelService {

    private final PapelRepository repository;
    private final PapelMapper mapper;

    public PapelService(PapelRepository repository, PapelMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public PapelDto criar(PapelDto dto) {
        if (repository.existsByCodigo(dto.codigo())) {
            throw new IllegalArgumentException("Já existe um papel com esse código.");
        }

        Papel papel = mapper.toEntity(dto);
        papel = repository.save(papel);
        return mapper.toDto(papel);
    }

    @Transactional
    public PapelDto atualizar(Long id, PapelDto dto) {
        Papel papel = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado."));

        // Se mudou o código, verifica duplicidade
        if (!papel.getCodigo().equals(dto.codigo())
                && repository.existsByCodigo(dto.codigo())) {
            throw new IllegalArgumentException("Já existe um papel com esse código.");
        }

        papel.setCodigo(dto.codigo());
        papel.setDescricao(dto.descricao());

        papel = repository.save(papel);
        return mapper.toDto(papel);
    }

    @Transactional(readOnly = true)
    public PapelDto buscarPorId(Long id) {
        Papel papel = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Papel não encontrado."));
        return mapper.toDto(papel);
    }

    @Transactional(readOnly = true)
    public Page<PapelDto> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Papel não encontrado.");
        }
        repository.deleteById(id);
    }
}
