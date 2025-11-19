package br.com.workingsafe.mapper;

import br.com.workingsafe.dto.EmpresaDto;
import br.com.workingsafe.model.Empresa;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(EmpresaDto dto) {
        Empresa empresa = new Empresa();
        empresa.setId(dto.id());
        empresa.setNome(dto.nome());
        empresa.setCnpj(dto.cnpj());
        empresa.setEmailContato(dto.emailContato());
        return empresa;
    }

    public EmpresaDto toDto(Empresa empresa) {
        return new EmpresaDto(
                empresa.getId(),
                empresa.getNome(),
                empresa.getCnpj(),
                empresa.getEmailContato()
        );
    }
}
