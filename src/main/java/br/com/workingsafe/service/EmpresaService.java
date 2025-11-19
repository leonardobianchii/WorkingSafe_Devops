package br.com.workingsafe.service;

import br.com.workingsafe.dto.EmpresaDto;
import br.com.workingsafe.mapper.EmpresaMapper;
import br.com.workingsafe.model.Empresa;
import br.com.workingsafe.repository.EmpresaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class EmpresaService {

    private final EmpresaRepository repository;
    private final EmpresaMapper mapper;

    public EmpresaService(EmpresaRepository repository, EmpresaMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Transactional
    public EmpresaDto criar(EmpresaDto dto) {
        if (repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe empresa com esse nome.");
        }

        Empresa empresa = mapper.toEntity(dto);
        empresa = repository.save(empresa);
        return mapper.toDto(empresa);
    }

    @Transactional
    public EmpresaDto atualizar(Long id, EmpresaDto dto) {
        Empresa empresa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));

        // se mudou o nome, valida duplicidade
        if (!empresa.getNome().equals(dto.nome()) && repository.existsByNome(dto.nome())) {
            throw new IllegalArgumentException("Já existe empresa com esse nome.");
        }

        empresa.setNome(dto.nome());
        empresa.setCnpj(dto.cnpj());
        empresa.setEmailContato(dto.emailContato());

        empresa = repository.save(empresa);
        return mapper.toDto(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaDto buscarPorId(Long id) {
        Empresa empresa = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Empresa não encontrada."));
        return mapper.toDto(empresa);
    }

    @Transactional(readOnly = true)
    public Page<EmpresaDto> listar(Pageable pageable) {
        return repository.findAll(pageable)
                .map(mapper::toDto);
    }

    @Transactional
    public void excluir(Long id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Empresa não encontrada.");
        }
        repository.deleteById(id);
    }

    // ============================
    // USADO NO UsuarioWebController
    // ============================
    @Transactional(readOnly = true)
    public List<EmpresaDto> listarTodas() {
        return repository.findAll()
                .stream()
                .map(mapper::toDto)
                .toList();
    }
}
